package com.devee.devhive.domain.project.review.dto;

import com.devee.devhive.domain.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
  private int totalScore;
  private LocalDateTime createDateTime;
  private List<EvaluationDto> evaluationDtoList;

  public static ReviewDto of(ProjectReview review, List<Evaluation> evaluationList) {
    return ReviewDto.builder()
        .reviewerId(review.getReviewerUser().getId())
        .targetUserId(review.getTargetUser().getId())
        .totalScore(review.getTotalScore())
        .createDateTime(review.getCreatedDate())
        .evaluationDtoList(evaluationList.stream().map(evaluation -> EvaluationDto.builder()
            .badgeDto(BadgeDto.from(evaluation.getBadge()))
            .point(evaluation.getPoint())
            .build()).collect(Collectors.toList()))
        .build();
  }
}
