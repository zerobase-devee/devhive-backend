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
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.global.exception.CustomException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExitVoteService {

  private final ApplicationEventPublisher eventPublisher;
  private final ProjectMemberExitVoteRepository exitVoteRepository;

  public ProjectMemberExitVote findById(Long projectMemberExitVoteId) {
    return exitVoteRepository.findById(projectMemberExitVoteId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_VOTE));
  }

  public List<ProjectMemberExitVote> findByProjectId(Long projectId) {
    return exitVoteRepository.findAllByProjectId(projectId);
  }

  @Transactional
  public void createExitVoteAndSendAlarm(Project project, Long registeringUserId, User targetUser, List<ProjectMember> members) {
    if (exitVoteRepository.existsByProjectId(project.getId())) {
      throw new CustomException(ALREADY_REGISTERED_VOTE);
    }

    Instant currentTime = Instant.now();
    List<ProjectMemberExitVote> exitVoteList = new ArrayList<>();

    for (ProjectMember member : members) {
      User user = member.getUser();
      // 퇴출대상자 제외
      if (Objects.equals(user.getId(), targetUser.getId())) {
        continue;
      }
      ProjectMemberExitVote exitVote = ProjectMemberExitVote.of(
          project, targetUser, user, currentTime);
      // 등록자의 투표는 자동으로 참여 및 찬성으로 처리
      if (Objects.equals(user.getId(), registeringUserId)) {
        exitVote.setAccept(true);
        exitVote.setVoted(true);
      }
      exitVoteList.add(exitVote);
    }

    exitVoteRepository.saveAll(exitVoteList);

    // 팀원(퇴출 대상자와 등록자 제외한)들에게 퇴출 투표 생성 알림 이벤트 발행
    for (ProjectMember projectMember : members) {
      User user = projectMember.getUser();
      if (Objects.equals(registeringUserId, user.getId())) {
        continue; // 등록자 알림 제외
      }
      alarmEventPub(user, project, AlarmContent.EXIT_VOTE, targetUser);
    }
  }

  // 투표 제출 및 결과 저장
  public void submitExitVote(ProjectMemberExitVote myVote, boolean vote) {
    if (myVote.isVoted()) {
      throw new CustomException(ALREADY_SUBMIT_VOTE);
    }

    myVote.setVoted(true);
    myVote.setAccept(vote);
    exitVoteRepository.save(myVote);
  }

  public int countVotedMembers(List<ProjectMemberExitVote> projectMemberExitVotes) {
    return (int) projectMemberExitVotes.stream().filter(ProjectMemberExitVote::isVoted).count();
  }

  public boolean resultTargetUserExit(List<ProjectMemberExitVote> exitVotes) {
    long agreedCount = exitVotes.stream().filter(ProjectMemberExitVote::isAccept).count();
    int totalVotes = exitVotes.size();

    return Math.round(totalVotes / 2.0) <= agreedCount;
  }

  // 열린지 24시간이 지난 투표 삭제 처리
  @Transactional
  public void processVotes() {
    List<ProjectMemberExitVote> closedVotes = exitVoteRepository.findAllByCreatedDateBefore(
        Instant.now().minus(1, ChronoUnit.DAYS));

    // 퇴출 실패 알림 이벤트 발행
    sendExitVoteFailAlarm(closedVotes);

    deleteAllVotes(closedVotes);
  }

  public void deleteAllVotes(List<ProjectMemberExitVote> exitVotes) {
    exitVoteRepository.deleteAll(exitVotes);
  }

  @Transactional
  public void sendExitVoteFailAlarm(List<ProjectMemberExitVote> exitVotes) {
    for (ProjectMemberExitVote memberExitVote : exitVotes) {
      alarmEventPub(memberExitVote.getVoterUser(), memberExitVote.getProject(),
          AlarmContent.VOTE_RESULT_EXIT_FAIL, memberExitVote.getTargetUser());
    }
  }

  private void alarmEventPub(User receiver, Project project, AlarmContent content, User user) {
    AlarmForm alarmForm = AlarmForm.builder()
        .receiverUser(receiver) // 투표 참여 팀원들
        .projectId(project.getId())
        .projectName(project.getName())
        .content(content)
        .user(user) // 퇴출 대상자
        .build();
    eventPublisher.publishEvent(alarmForm);
  }
}
