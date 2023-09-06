package com.devee.devhive.domain.project.review.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.dto.ReviewDto;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.devee.devhive.domain.project.review.evaluation.service.EvaluationService;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.project.service.ProjectService;
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
  private final EvaluationService evaluationService;
  private final UserService userService;
  private final ProjectService projectService;

  @PostMapping("{projectId}/review/{targetUserId}")
  @Operation(summary = "프로젝트 완료 후 리뷰 작성")
  public ResponseEntity<ReviewDto> submitReview(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable Long projectId, @PathVariable Long targetUserId,
      @RequestBody EvaluationForm form
  ) {
    User user = userService.getUserByEmail(principalDetails.getEmail());
    User targetUser = userService.getUserById(targetUserId);
    Project project = projectService.findById(projectId);

    ProjectReview newReview = reviewService.submitReview(projectId, targetUserId, user, project, targetUser, form);
    List<Evaluation> evaluationList = evaluationService.saveAllEvaluations(newReview, form);

    int count = reviewService.countAllByProjectIdAndTargetUserId(projectId, targetUserId);
    if (count == project.getTeamSize()-1) {
      // 팀원평가 평균점수를 타겟유저 랭킹포인트에 더하기
      double averagePoint = reviewService.getAverageTotalScoreByTargetUserAndProject(targetUserId, projectId);
      userService.updateRankPoint(targetUser, project, averagePoint);
    }
      return ResponseEntity.ok(ReviewDto.of(newReview, evaluationList));
  }
}
