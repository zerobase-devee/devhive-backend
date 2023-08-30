package com.devee.devhive.domain.project.vote.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_REGISTERED_VOTE;
import static com.devee.devhive.global.exception.ErrorCode.ALREADY_SUBMIT_VOTE;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_VOTE;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.repository.ProjectMemberExitVoteRepository;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.exithistory.entity.ExitHistory;
import com.devee.devhive.domain.user.exithistory.repository.ExitHistoryRepository;
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.global.exception.CustomException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExitVoteService {

  private final ApplicationEventPublisher eventPublisher;
  private final ProjectMemberExitVoteRepository exitVoteRepository;
  private final ExitHistoryRepository exitHistoryRepository;

  public List<ProjectMemberExitVote> findByProjectId(Long projectId) {
    return exitVoteRepository.findAllByProjectId(projectId);
  }

  public String createExitVote(Project project, User registeringUser, User targetUser, List<ProjectMember> votingUsers) {
    if (exitVoteRepository.existsByProjectId(project.getId())) {
      throw new CustomException(ALREADY_REGISTERED_VOTE);
    }

    Instant currentTime = Instant.now();
    List<ProjectMemberExitVote> exitVoteList = new ArrayList<>();

    for (ProjectMember member : votingUsers) {
      ProjectMemberExitVote exitVote = ProjectMemberExitVote.of(project, targetUser,
          member.getUser(), currentTime);
      // 등록자의 투표는 자동으로 참여 및 찬성으로 처리
      if (exitVote.getVoterUser().getId().equals(registeringUser.getId())) {
        exitVote.setAccept(true);
        exitVote.setVoted(true);
      }
      exitVoteList.add(exitVote);
    }

    exitVoteRepository.saveAllAndFlush(exitVoteList);

    // 팀원(퇴출 대상자와 등록자 제외한)들에게 퇴출 투표 생성 알림 이벤트 발행
    for (ProjectMember projectMember : votingUsers) {
      if (Objects.equals(registeringUser.getId(), projectMember.getUser().getId())) {
        continue; // 등록자 알림 제외
      }
      AlarmForm alarmForm = AlarmForm.builder()
          .receiverUser(projectMember.getUser()) // 팀원들
          .project(project)
          .content(AlarmContent.EXIT_VOTE)
          .user(targetUser) // 퇴출 대상자
          .build();
      eventPublisher.publishEvent(alarmForm);
    }

    return targetUser.getNickName() + " 유저에 대한 퇴출 투표 생성이 완료되었습니다.";
  }

  // 투표 제출 및 결과 저장
  public ProjectMemberExitVote submitExitVote(Project project, User votingUser, User targetUser, boolean vote) {
    ProjectMemberExitVote myVote = getMyVote(project.getId(), votingUser.getId(),
        targetUser.getId());

    if (myVote.isVoted()) {
      throw new CustomException(ALREADY_SUBMIT_VOTE);
    }

    myVote.setVoted(true);
    myVote.setAccept(vote);
    return exitVoteRepository.save(myVote);
  }

  public ProjectMemberExitVote getMyVote(Long projectId, Long votingUserId, Long targetUserId) {
    return exitVoteRepository.findByProjectIdAndVoterUserIdAndTargetUserId(projectId, votingUserId,
        targetUserId).orElseThrow(() -> new CustomException(NOT_FOUND_VOTE));
  }

  // 열린지 24시간이 지난 투표 찾기
  private List<ProjectMemberExitVote> findAllClosedVotes() {
    return exitVoteRepository.findAllByCreatedDateBefore(
        Instant.now().minus(1, ChronoUnit.DAYS));
  }

  // 각 프로젝트 별로 진행중인 투표 묶기
  public Map<Long, List<ProjectMemberExitVote>> getSortedVotes() {
    List<ProjectMemberExitVote> closedVotes = findAllClosedVotes();
    Map<Long, List<ProjectMemberExitVote>> sortedVotesMap = new HashMap<>();

    for (ProjectMemberExitVote vote : closedVotes) {
      Long projectId = vote.getProject().getId();
      sortedVotesMap.computeIfAbsent(projectId, key -> new ArrayList<>()).add(vote);
    }

    return sortedVotesMap;
  }

  // 투표 결과 처리
  public Map<Long, ExitHistory> processVotes(Map<Long, List<ProjectMemberExitVote>> sortedVotesMap) {
    Map<Long, ExitHistory> exitHistoryMap = new HashMap<>();
    for (long projectId : sortedVotesMap.keySet()) {
      List<ProjectMemberExitVote> currentVotes = sortedVotesMap.get(projectId);
      User targetUser = currentVotes.get(0).getTargetUser();
      int teamSize = currentVotes.get(0).getProject().getTeamSize();
      int votedCount = currentVotes.size();
      int exitedCount = exitHistoryRepository.countExitHistoryByUserId(targetUser.getId());

      if (votedCount == teamSize - 1) {
        int agreedCount = (int) currentVotes.stream()
            .filter(ProjectMemberExitVote::isAccept)
            .count();
        if (isOverHalf(votedCount, agreedCount)) {
          log.info("투표가 과반수를 넘었으므로 해당 유저를 퇴출 처리합니다.");
          ExitHistory exitHistory = ExitHistory.builder()
              .user(targetUser)
              .reActiveDate(Instant.now().plus(exitedCount+1, ChronoUnit.WEEKS))
              .build();
          exitHistoryMap.put(projectId, exitHistory);
        }
      } else {
        log.info("투표에 전부 참여하지 않아 처리할 수 없습니다.");
      }

      log.info("{} 프로젝트 전체 인원 : {}, 투표 참여 인원 : {}", projectId, teamSize, votedCount);
      exitVoteRepository.deleteAll(currentVotes);
    }
    return exitHistoryMap;
  }
  // 과반수를 넘었는지 확인
  private boolean isOverHalf(int a, int b) {
    return Math.round(a / 2.0) <= b;
  }
}
