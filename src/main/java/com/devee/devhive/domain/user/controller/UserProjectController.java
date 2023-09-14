package com.devee.devhive.domain.user.controller;

import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.service.ChatRoomService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.dto.MyProjectInfoDto;
import com.devee.devhive.domain.project.entity.dto.SimpleProjectDto;
import com.devee.devhive.domain.project.member.entity.dto.ProjectMemberDto;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@Tag(name = "USER PROJECT API", description = "유저 프로젝트 API")
public class UserProjectController {

    private final UserService userService;
    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final ProjectReviewService projectReviewService;
    private final ChatRoomService chatRoomService;

    // 내가 생성한 프로젝트 페이지
    @GetMapping("/write")
    @Operation(summary = "내가 생성한 프로젝트 목록 조회")
    public ResponseEntity<Page<SimpleProjectDto>> getWriteProjects(
        @AuthenticationPrincipal PrincipalDetails principal, Pageable pageable
    ) {
        User user = userService.getUserByEmail(principal.getEmail());
        return ResponseEntity.ok(
            projectService.getWriteProjects(user.getId(), pageable).map(SimpleProjectDto::from)
        );
    }

    // 내가 참여한 프로젝트 페이지
    @GetMapping("/participation")
    @Operation(summary = "내가 참여한 프로젝트 목록 조회")
    public ResponseEntity<Page<SimpleProjectDto>> getParticipationProjects(
        @AuthenticationPrincipal PrincipalDetails principal, Pageable pageable
    ) {
        User user = userService.getUserByEmail(principal.getEmail());

        List<SimpleProjectDto> projectDtoList = projectMemberService.getParticipationProjects(user.getId(), pageable)
            .stream()
            .filter(projectMember -> !projectMember.isLeader())
            .map(projectMember -> SimpleProjectDto.from(projectMember.getProject()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(projectDtoList, pageable, projectDtoList.size()));
    }

    // 내 프로젝트 정보 조회
    @GetMapping("/{projectId}")
    @Operation(summary = "내 프로젝트 상세 정보 조회")
    public ResponseEntity<MyProjectInfoDto> getProjectInfo(
        @PathVariable("projectId") Long projectId,
        @AuthenticationPrincipal PrincipalDetails principal
    ) {
        User user = userService.getUserByEmail(principal.getEmail());
        Long userId = user.getId();
        Project project = projectService.findById(projectId);
        long memberCount = projectMemberService.getProjectMemberByProjectId(projectId).size();
        double totalAverageScore = projectReviewService.getAverageTotalScoreByTargetUserAndProject(userId, projectId, memberCount);
        List<ProjectMemberDto> projectMemberDtoList =
            projectMemberService.getProjectMemberByProjectId(projectId).stream()
                .map(projectMember -> {
                    boolean isReviewed = projectReviewService.isReviewed(projectId, userId, projectMember.getUser().getId());
                    return ProjectMemberDto.of(projectMember, isReviewed);
                }).toList();

        boolean leader = Objects.equals(project.getUser().getId(), userId);
        ProjectChatRoom chatRoom = chatRoomService.findByProjectId(projectId);
        Long roomId = chatRoom == null ? null : chatRoom.getId();
        return ResponseEntity.ok(
            MyProjectInfoDto.of(userId, project, projectMemberDtoList, totalAverageScore, leader, roomId)
        );
    }
}
