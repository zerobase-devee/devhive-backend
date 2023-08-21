package com.devee.devhive.domain.project.review.dto;

import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty(value = "reviewer_id")
  private Long reviewerId;
  @JsonProperty(value = "target_user_id")
  private Long targetUserId;

  private int manner;
  private int contribution;
  private int communication;
  private int schedule;
  private int professionalism;

  @JsonProperty(value = "total_score")
  private int totalScore;

  @JsonProperty(value = "create_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
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
