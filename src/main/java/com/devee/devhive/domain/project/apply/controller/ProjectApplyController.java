package com.devee.devhive.domain.project.apply.controller;

import static com.devee.devhive.global.exception.ErrorCode.RECRUITMENT_ALREADY_COMPLETED;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.apply.entity.ProjectApply;
import com.devee.devhive.domain.project.apply.entity.dto.ApplicantUserDto;
import com.devee.devhive.domain.project.apply.service.ProjectApplyService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.type.ApplyStatus;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "PROJECT APPLY API", description = "프로젝트 참가 신청 API")
public class ProjectApplyController {

    private final UserService userService;
    private final ProjectApplyService projectApplyService;
    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

    // 프로젝트 참가 신청
    @PostMapping("/{projectId}/application")
    @Operation(summary = "프로젝트 참가 신청", description = "프로젝트 고유 ID로 프로젝트 참가 신청")
    public void projectApply(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        Project project = projectService.findById(projectId);
        projectApplyService.projectApplyAndSendAlarmToProjectUser(user, project);
    }

    // 신청 취소
    @DeleteMapping("/{projectId}/application")
    @Operation(summary = "프로젝트 신청 취소", description = "프로젝트 고유 ID로 프로젝트 참가 신청 취소")
    public void deleteApplication(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        projectApplyService.deleteApplication(user.getId(), projectId);
    }

    // 프로젝트 신청자 목록 조회
    @GetMapping("/{projectId}/application")
    @Operation(summary = "프로젝트 신청자 목록 조회", description = "프로젝트 고유 ID로 프로젝트 신청자 목록 조회")
    public ResponseEntity<List<ApplicantUserDto>> getApplicants(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        Project project = projectService.findById(projectId);
        if (!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }
        List<ProjectApply> projectApplies = projectApplyService.getProjectApplies(projectId);
        return ResponseEntity.ok(projectApplies.stream()
            .filter(projectApply -> projectApply.getStatus() == ApplyStatus.PENDING)
            .map(projectApply -> ApplicantUserDto.of(projectApply.getUser(), projectApply.getId()))
            .collect(Collectors.toList()));
    }

    // 신청 승인
    @PutMapping("/application/{applicationId}/accept")
    @Operation(summary = "프로젝트 신청 승인")
    public void accept(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("applicationId") Long applicationId
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        ProjectApply projectApply = projectApplyService.getProjectApplyById(applicationId);
        Project project = projectApply.getProject();

        // 프로젝트 작성자가 아닌 경우
        if (!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }

        // 프로젝트 상태가 모집 완료된 상태인 경우
        ProjectStatus status = project.getStatus();
        if (status == ProjectStatus.COMPLETE || status == ProjectStatus.RECRUITMENT_COMPLETE) {
            throw new CustomException(RECRUITMENT_ALREADY_COMPLETED);
        }

        // 승인 전 팀원 수 체크, 이미 팀원이 다 찬 경우 예외
        int teamSize = project.getTeamSize();
        int memberNums = projectMemberService.countAllByProjectId(project.getId());
        if (memberNums >= teamSize) {
            throw new CustomException(RECRUITMENT_ALREADY_COMPLETED);
        }
        // 승인
        projectApplyService.acceptAndSendAlarmToApplicant(projectApply);
        // 프로젝트 멤버 저장
        projectMemberService.saveProjectMember(projectApply.getUser(), project);
        // 프로젝트 팀사이즈 다 찬 경우 프로젝트 모집마감 상태로 변경
        if (memberNums + 1 == teamSize) {
            projectService.updateProjectStatusRecruitmentComplete(project);
        }
    }

    // 신청 거절
    @PutMapping("/application/{applicationId}/reject")
    @Operation(summary = "프로젝트 신청 거절")
    public void reject(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("applicationId") Long applicationId
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        projectApplyService.rejectAndSendAlarmToApplicant(user, applicationId);
    }
}
