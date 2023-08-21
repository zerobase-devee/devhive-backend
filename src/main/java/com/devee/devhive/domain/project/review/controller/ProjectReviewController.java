package com.devee.devhive.domain.project.review.controller;

import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.dto.ReviewDto;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.security.service.PrincipalDetails;
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

  @PostMapping("{projectId}/review/{targetUserId}")
  public ResponseEntity<ReviewDto> submitReview(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable Long projectId, @PathVariable Long targetUserId,
      @RequestBody EvaluationForm form
  ) {
    User user = principalDetails.getUser();

    return ResponseEntity.ok(reviewService.submitReview(user, projectId, targetUserId, form));
  }
}
