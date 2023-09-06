package com.devee.devhive.domain.project.review.evaluation.service;

import com.devee.devhive.domain.badge.entity.Badge;
import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.devee.devhive.domain.project.review.evaluation.repository.EvaluationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationService {

  private final EvaluationRepository evaluationRepository;

  public List<Evaluation> saveAllEvaluations(ProjectReview review, List<EvaluationForm> forms) {
    List<Evaluation> evaluationList = forms.stream()
        .map(evaluationForm -> Evaluation.builder()
            .projectReview(review)
            .badge(Badge.from(evaluationForm.getBadgeDto()))
            .point(evaluationForm.getPoint())
            .build()).collect(Collectors.toList());

    return evaluationRepository.saveAll(evaluationList);
  }
}
