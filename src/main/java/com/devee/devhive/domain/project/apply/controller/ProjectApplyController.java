package com.devee.devhive.domain.project.apply.controller;

import static com.devee.devhive.global.exception.ErrorCode.RECRUITMENT_ALREADY_COMPLETED;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.apply.entity.ProjectApply;
import com.devee.devhive.domain.project.apply.entity.dto.ApplicantUserDto;
import com.devee.devhive.domain.project.apply.service.ProjectApplyService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.type.ProjectStatus;
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
            throw new CustomException(UNAUTHORIZED);
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
        ProjectApply projectApply = projectApplyService.getProjectApplyById(applicationId);
        Project project = projectApply.getProject();

        // 프로젝트 작성자가 아닌 경우
        if (!Objects.equals(project.getWriterUser().getId(), user.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }

        // 프로젝트 상태가 모집 완료된 상태인 경우
        ProjectStatus status = project.getStatus();
        if (status == ProjectStatus.COMPLETE || status == ProjectStatus.RECRUITMENT_COMPLETE) {
            throw new CustomException(RECRUITMENT_ALREADY_COMPLETED);
        }

        // 승인 전 참가인원 체크, 이미 팀원이 다 찬 경우 예외
        if (!projectMemberService.availableAccept(project)) {
            throw new CustomException(RECRUITMENT_ALREADY_COMPLETED);
        }
        // 승인
        projectApplyService.accept(projectApply);
        // 프로젝트 멤버 저장
        projectMemberService.saveProjectMember(projectApply.getUser(), project);
    }

    // 신청 거절
    @PutMapping("/application/{applicationId}/reject")
    public void reject(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("applicationId") Long applicationId
    ) {
        User user = principalDetails.getUser();
        projectApplyService.reject(user, applicationId);
    }
}
