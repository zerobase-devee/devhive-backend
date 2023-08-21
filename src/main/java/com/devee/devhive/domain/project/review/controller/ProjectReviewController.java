package com.devee.devhive.domain.project.review.controller;

import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.dto.ReviewDto;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.devee.devhive.domain.project.review.evaluation.service.EvaluationService;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.security.service.PrincipalDetails;
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
public class ProjectReviewController {

  private final ProjectReviewService reviewService;
  private final EvaluationService evaluationService;

  @PostMapping("{projectId}/review/{targetUserId}")
  public ResponseEntity<ReviewDto> submitReview(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable Long projectId, @PathVariable Long targetUserId,
      @RequestBody EvaluationForm form
  ) {
    User user = principalDetails.getUser();

    ProjectReview newReview = reviewService.submitReview(user, projectId, targetUserId, form);
    List<Evaluation> evaluationList = evaluationService.saveAllEvaluations(newReview, form);

    return ResponseEntity.ok(ReviewDto.of(newReview, evaluationList));
  }
}
