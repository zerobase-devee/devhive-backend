package com.devee.devhive.domain.project.review.dto;

import com.devee.devhive.domain.badge.entity.dto.BadgeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationForm {

  private BadgeDto badgeDto;
  private int point;
}
