package com.devee.devhive.domain.project.apply.controller;

import static com.devee.devhive.global.exception.ErrorCode.NOT_PROJECT_WRITER;

import com.devee.devhive.domain.project.apply.entity.ProjectApply;
import com.devee.devhive.domain.project.apply.entity.dto.ApplicantUserDto;
import com.devee.devhive.domain.project.apply.service.ProjectApplyService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.security.service.PrincipalDetails;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectApplyController {

    private final ProjectApplyService projectApplyService;
    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

    // 프로젝트 참가 신청
    @PostMapping("/{projectId}/application")
    public void projectApply(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId
    ) {
        User user = principalDetails.getUser();
        Project project = projectService.findById(projectId);
        projectApplyService.projectApply(user, project);
    }

    // 신청 취소
    @DeleteMapping("/{projectId}/application")
    public void deleteApplication(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId
    ) {
        User user = principalDetails.getUser();
        projectApplyService.deleteApplication(user.getId(), projectId);
    }

    // 프로젝트 신청자 목록 조회
    @GetMapping("/{projectId}/application")
    public ResponseEntity<List<ApplicantUserDto>> getApplicants(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId
    ) {
        User user = principalDetails.getUser();
        Project project = projectService.findById(projectId);
        if (!Objects.equals(project.getWriterUser().getId(), user.getId())) {
            throw new CustomException(NOT_PROJECT_WRITER);
        }
        List<ProjectApply> projectApplies = projectApplyService.getProjectApplies(projectId);
        return ResponseEntity.ok(projectApplies.stream()
            .map(projectApply -> ApplicantUserDto.of(projectApply.getUser(), projectApply.getId()))
            .collect(Collectors.toList()));
    }

    // 신청 승인
    @PutMapping("/application/{applicationId}/accept")
    public void accept(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("applicationId") Long applicationId
    ) {
        User user = principalDetails.getUser();
        // 승인
        ProjectApply projectApply = projectApplyService.accept(user, applicationId);
        // 프로젝트 멤버 저장
        projectMemberService.saveProjectMember(projectApply.getUser(), projectApply.getProject());
    }

    // 신청 거절
    @PutMapping("/application/{applicationId}/accept")
    public void reject(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("applicationId") Long applicationId
    ) {
        User user = principalDetails.getUser();
        projectApplyService.reject(user, applicationId);
    }
}
