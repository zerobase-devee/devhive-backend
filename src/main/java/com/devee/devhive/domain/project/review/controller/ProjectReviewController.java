package com.devee.devhive.domain.project.review.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.dto.ReviewDto;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.devee.devhive.domain.project.review.evaluation.service.EvaluationService;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.badge.service.UserBadgeService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 프로젝트 멤버 리뷰 Controller
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "PROJECT REVIEW API", description = "프로젝트 리뷰 API")
public class ProjectReviewController {

  private final ProjectReviewService reviewService;
  private final ProjectMemberService memberService;
  private final EvaluationService evaluationService;
  private final UserService userService;
  private final ProjectService projectService;
  private final UserBadgeService userBadgeService;

  @PostMapping("{projectId}/review/{targetUserId}")
  @Operation(summary = "프로젝트 완료 후 리뷰 작성")
  public ResponseEntity<ReviewDto> submitReview(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable(name = "projectId") Long projectId, @PathVariable(name = "targetUserId") Long targetUserId,
      @RequestBody List<EvaluationForm> forms
  ) {
    User user = userService.getUserByEmail(principalDetails.getEmail());
    User targetUser = userService.getUserById(targetUserId);
    Project project = projectService.findById(projectId);

    ProjectReview newReview = reviewService.submitReview(projectId, targetUserId, user, project, targetUser, forms);
    List<Evaluation> evaluationList = evaluationService.saveAllEvaluations(newReview, forms);

    // 타겟유저의 유저뱃지리스트들 점수 업데이트
    userBadgeService.updatePoint(targetUser, evaluationList);

    int memberCount = memberService.getProjectMemberByProjectId(projectId).size();
    // 팀원평가 모두 한 경우 평균점수를 타겟유저 랭킹포인트 업데이트
    Double averagePoint = reviewService.getAverageTotalScoreByTargetUserAndProject(targetUserId, projectId, memberCount);
    if (averagePoint != null) {
      userService.updateRankPoint(targetUser, project, averagePoint);
    }
    return ResponseEntity.ok(ReviewDto.of(newReview, evaluationList));
  }
}
