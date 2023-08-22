package com.devee.devhive.domain.project.review.dto;

import com.devee.devhive.domain.project.type.EvaluationItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationForm {

  private int manner;
  private int contribution;
  private int communication;
  private int schedule;
  private int professionalism;

  public int getTotalScore() {
    return manner + contribution + communication + schedule + professionalism;
  }

  public int getValue(EvaluationItem item) {
    return switch (item) {
      case MANNER -> manner;
      case CONTRIBUTION -> contribution;
      case COMMUNICATION -> communication;
      case SCHEDULE -> schedule;
      case PROFESSIONALISM -> professionalism;
    };
  }
}
