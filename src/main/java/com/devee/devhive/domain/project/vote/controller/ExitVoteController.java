package com.devee.devhive.domain.project.vote.controller;

import static com.devee.devhive.global.exception.ErrorCode.NOT_YOUR_VOTE;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.vote.dto.ProjectExitVoteDto;
import com.devee.devhive.domain.project.vote.dto.VoteDto;
import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.service.ExitVoteService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.exithistory.entity.ExitHistory;
import com.devee.devhive.domain.user.exithistory.service.ExitHistoryService;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.domain.user.type.ActivityStatus;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/vote")
@RequiredArgsConstructor
@Tag(name = "EXIT VOTE API", description = "프로젝트 퇴출 투표 API")
public class ExitVoteController {

  private final ExitVoteService exitVoteService;
  private final UserService userService;
  private final ProjectService projectService;
  private final ProjectMemberService projectMemberService;
  private final ExitHistoryService exitHistoryService;

  @PostMapping("/{targetUserId}")
  @Operation(summary = "프로젝트 퇴출 투표 생성")
  public ResponseEntity<String> createExitVote(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable Long projectId, @PathVariable Long targetUserId
  ) {
    User registeringUser = userService.getUserByEmail(principalDetails.getEmail());
    User targetUser = userService.getUserById(targetUserId);
    Project project = projectService.findById(projectId);

    if (!projectMemberService.isMemberofProject(projectId, registeringUser.getId())) {
      throw new CustomException(ErrorCode.NOT_YOUR_PROJECT);
    }

    // 투표 대상 유저를 제외한 모든 유저
    List<ProjectMember> votingUsers = projectMemberService.getProjectMemberByProjectId(projectId).stream()
        .filter(member -> !Objects.equals(member.getUser().getId(), targetUser.getId()))
        .collect(Collectors.toList());

    return ResponseEntity.ok(
        exitVoteService.createExitVoteAndSendAlarm(project, registeringUser, targetUser, votingUsers));
  }

  @PutMapping("/{voteId}")
  @Operation(summary = "프로젝트 퇴출 투표 제출")
  public ResponseEntity<VoteDto> submitExitVote(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable(name = "projectId") Long projectId, @PathVariable(name = "voteId") Long voteId, @RequestParam boolean vote
  ) {
    User voterUser = userService.getUserByEmail(principalDetails.getEmail());
    ProjectMemberExitVote myVote = exitVoteService.findById(voteId);
    if (!Objects.equals(myVote.getVoterUser().getId(), voterUser.getId())) {
      throw new CustomException(NOT_YOUR_VOTE);
    }
    Project project = myVote.getProject();
    User targetUser = myVote.getTargetUser();
    Long targetUserId = targetUser.getId();

    // 투표 제출
    exitVoteService.submitExitVote(myVote, vote);
    // 모든 팀원이 투표완료한 경우
    List<ProjectMemberExitVote> exitVotes  = exitVoteService.findByProjectId(projectId);
    int countVotedMembers = exitVoteService.countVotedMembers(exitVotes);
    if (countVotedMembers == exitVotes.size()) {
      // 타겟 유저 퇴출전적
      int exitedCount = exitHistoryService.countExitHistoryByUserId(targetUserId);
      boolean isTargetUserExit = exitVoteService.resultTargetUserExit(exitVotes);
      if (isTargetUserExit) {
        // 퇴출 횟수 당 1주로 유저 비활성화 기간 설정(이번이 10회째인 경우 영구 비활성화)
        Instant reActiveDate = exitedCount >= 9 ?
            Instant.MAX : Instant.now().plus(exitedCount + 1, ChronoUnit.WEEKS);

        exitHistoryService.saveExitHistory(ExitHistory.builder()
            .user(targetUser)
            .reActiveDate(reActiveDate)
            .build());
        userService.setUserStatus(targetUser, ActivityStatus.INACTIVITY);

        // 퇴출자가 프로젝트의 리더인 경우
        if (Objects.equals(project.getUser().getId(), targetUserId)) {
          projectMemberService.deleteAllOfMembersFromProjectAndSendAlarm(projectId);
          projectService.deleteLeadersProject(projectId);
        } else {
          // 퇴출자만 팀에서 삭제, 알림 발행
          projectMemberService.deleteMemberFromProjectAndSendAlarm(projectId, targetUserId);
        }
      } else {
        // 퇴출 실패 알림
        exitVoteService.sendExitVoteFailAlarm(exitVotes);
      }
      // 투표 결과 처리 후 삭제
      exitVoteService.deleteAllVotes(exitVotes);
    }
    return ResponseEntity.ok(VoteDto.from(myVote));
  }

  // 프로젝트에 퇴출투표가 있으면 투표정보와 각 팀원들의 투표 참여 여부
  @GetMapping
  @Operation(summary = "프로젝트 퇴출 투표 정보 조회")
  public ResponseEntity<List<ProjectExitVoteDto>> getExitVote(@PathVariable Long projectId) {
    List<ProjectMemberExitVote> exitVotes = exitVoteService.findByProjectId(projectId);
    return ResponseEntity.ok(exitVotes.stream()
        .map(ProjectExitVoteDto::from)
        .collect(Collectors.toList())
    );
  }
}
