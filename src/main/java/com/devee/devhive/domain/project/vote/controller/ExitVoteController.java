package com.devee.devhive.domain.project.vote.controller;

import static com.devee.devhive.global.exception.ErrorCode.NOT_YOUR_VOTE;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.vote.dto.VoteDto;
import com.devee.devhive.domain.project.vote.dto.ProjectExitVoteDto;
import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.service.ExitVoteService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

  @PostMapping("/{targetUserId}")
  @Operation(summary = "프로젝트 퇴출 투표 생성")
  public ResponseEntity<VoteDto> createExitVote(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable Long projectId, @PathVariable Long targetUserId
  ) {
    User registeringUser = userService.getUserByEmail(principalDetails.getEmail());
    User targetUser = userService.getUserById(targetUserId);
    Project project = projectService.findById(projectId);

    if (projectMemberService.isNotMemberOfProject(projectId, registeringUser.getId())) {
      throw new CustomException(ErrorCode.NOT_YOUR_PROJECT);
    }

    // 팀원이 2명뿐인 경우 투표 생성 하지 않음
    if (project.getTeamSize() == 2) {
      return ResponseEntity.ok(VoteDto.of(project, targetUserId));
    }

    List<ProjectMember> members = projectMemberService.getProjectMemberByProjectId(projectId);

    exitVoteService.createExitVoteAndSendAlarm(project, registeringUser.getId(), targetUser, members);
    return ResponseEntity.ok(null);
  }

  @PutMapping("/{voteId}")
  @Operation(summary = "프로젝트 퇴출 투표 제출")
  public ResponseEntity<VoteDto> submitExitVote(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable(name = "projectId") Long projectId,
      @PathVariable(name = "voteId") Long voteId, @RequestParam boolean vote
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

    List<ProjectMemberExitVote> exitVotes = exitVoteService.findByProjectId(projectId);
    // 투표 참여한 팀원 수
    int countVotedMembers = exitVoteService.countVotedMembers(exitVotes);
    // 모든 팀원이 투표완료한 경우
    if (countVotedMembers == exitVotes.size()) {
      boolean isTargetUserExit = exitVoteService.resultTargetUserExit(exitVotes);
      if (isTargetUserExit) {
        exitVoteService.deleteAllVotes(exitVotes);
        return ResponseEntity.ok(VoteDto.of(project, targetUserId));
      } else {
        // 퇴출 실패 알림
        exitVoteService.sendExitVoteFailAlarm(exitVotes);
      }
      exitVoteService.deleteAllVotes(exitVotes);
    }
    return ResponseEntity.ok(null);
  }

  // 프로젝트에 퇴출투표가 있으면 투표정보와 각 팀원들의 투표 참여 여부
  @GetMapping
  @Operation(summary = "프로젝트 퇴출 투표 정보 조회")
  public ResponseEntity<List<ProjectExitVoteDto>> getExitVote(@PathVariable Long projectId) {
    List<ProjectMemberExitVote> exitVotes = exitVoteService.findByProjectId(projectId);
    return ResponseEntity.ok(exitVotes.stream()
        .map(ProjectExitVoteDto::from)
        .collect(Collectors.toList()));
  }
}
