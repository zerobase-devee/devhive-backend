package com.devee.devhive.domain.project.vote.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.vote.dto.VoteDto;
import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.service.ExitVoteService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.exception.ErrorCode;
import com.devee.devhive.global.entity.PrincipalDetails;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/vote/{targetUserId}")
@RequiredArgsConstructor
public class ExitVoteController {

  private final ExitVoteService exitVoteService;
  private final UserService userService;
  private final ProjectService projectService;
  private final ProjectMemberService projectMemberService;

  @PostMapping
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
        exitVoteService.createExitVote(project, registeringUser, targetUser, votingUsers));
  }

  @PutMapping
  public ResponseEntity<VoteDto> submitExitVote(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable Long projectId, @PathVariable Long targetUserId, @RequestParam boolean vote
  ) {
    User votingUser = userService.getUserByEmail(principalDetails.getEmail());
    User targetUser = userService.getUserById(targetUserId);
    Project project = projectService.findById(projectId);

    if (!projectMemberService.isMemberofProject(projectId, votingUser.getId())) {
      throw new CustomException(ErrorCode.NOT_YOUR_PROJECT);
    }

    ProjectMemberExitVote myVote = exitVoteService.submitExitVote(project, votingUser, targetUser, vote);

    return ResponseEntity.ok(VoteDto.from(myVote));
  }
}
