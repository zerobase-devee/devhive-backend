package com.devee.devhive.domain.project.vote.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.vote.service.ExitVoteService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ExitVoteController {

  private final ExitVoteService exitVoteService;
  private final UserService userService;
  private final ProjectService projectService;
  private final ProjectMemberService projectMemberService;

  @PostMapping("{projectId}/vote/{targetUserId}")
  public ResponseEntity<String> createExitVote(
      @PathVariable Long projectId,
      @PathVariable Long targetUserId
  ) {
    User targetUser = userService.getUserById(targetUserId);
    Project project = projectService.findById(projectId);
    List<ProjectMember> votingUsers = projectMemberService.getProjectMemberByProjectId(projectId)
        .stream()
        .filter(member -> !Objects.equals(member.getUser().getId(), targetUser.getId()))
        .collect(Collectors.toList());

    return ResponseEntity.ok(exitVoteService.createExitVote(project, targetUser, votingUsers));
  }
}
