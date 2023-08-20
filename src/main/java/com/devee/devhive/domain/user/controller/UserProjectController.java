package com.devee.devhive.domain.user.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.dto.MyProjectInfoDto;
import com.devee.devhive.domain.project.entity.dto.SimpleProjectDto;
import com.devee.devhive.domain.project.member.entity.dto.ProjectMemberDto;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 - 내 프로젝트
 */
@RestController
@RequestMapping("/api/users/project")
@RequiredArgsConstructor
public class UserProjectController {

    private final UserService userService;
    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final ProjectReviewService projectReviewService;

    // 내가 생성한 프로젝트 페이지
    @GetMapping("/write")
    public ResponseEntity<Page<SimpleProjectDto>> getWriteProjects(Principal principal) {
        Pageable pageable = PageRequest.of(0, 3);
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(
            projectService.getWriteProjects(user.getId(), pageable)
            .map(SimpleProjectDto::from)
        );
    }

    // 내가 참여한 프로젝트 페이지
    @GetMapping("/participation")
    public ResponseEntity<Page<SimpleProjectDto>> getParticipationProjects(Principal principal) {
        Pageable pageable = PageRequest.of(0, 3);
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(
            projectMemberService.getParticipationProjects(user.getId(), pageable)
                .map(projectMember -> SimpleProjectDto.from(projectMember.getProject()))
        );
    }

    // 내 프로젝트 정보 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<MyProjectInfoDto> getProjectInfo(
        @PathVariable("projectId") Long projectId, Principal principal
    ) {
        User user = userService.getUserByEmail(principal.getName());
        Project project = projectService.findById(projectId);
        double totalAverageScore =
            projectReviewService.getAverageTotalScoreByTargetUserAndProject(user.getId(), projectId);
        List<ProjectMemberDto> projectMemberDtoList =
            projectMemberService.getProjectMemberByProjectId(projectId)
            .stream().map(ProjectMemberDto::from).toList();

        return ResponseEntity.ok(
            MyProjectInfoDto.of(project, projectMemberDtoList, totalAverageScore)
        );
    }
}
