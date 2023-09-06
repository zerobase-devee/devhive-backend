package com.devee.devhive.domain.project.review.dto;

import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

  private Long reviewerId;
  private Long targetUserId;
  private int manner;
  private int contribution;
  private int communication;
  private int schedule;
  private int professionalism;
  private int totalScore;
  private LocalDateTime createDateTime;

  public static ReviewDto of(ProjectReview review, List<Evaluation> evaluationList) {
    return ReviewDto.builder()
        .reviewerId(review.getReviewerUser().getId())
        .targetUserId(review.getTargetUser().getId())
        .manner(evaluationList.get(0).getPoint())
        .contribution(evaluationList.get(1).getPoint())
        .communication(evaluationList.get(2).getPoint())
        .schedule(evaluationList.get(3).getPoint())
        .professionalism(evaluationList.get(4).getPoint())
        .totalScore(review.getTotalScore())
        .createDateTime(review.getCreatedDate())
        .build();
  }
}
