package com.devee.devhive.domain.project.review.evaluation.service;

import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.devee.devhive.domain.project.review.evaluation.repository.EvaluationRepository;
import com.devee.devhive.domain.project.type.EvaluationItem;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationService {

  private final EvaluationRepository evaluationRepository;

  public List<Evaluation> saveAllEvaluations(ProjectReview review, EvaluationForm form) {
    List<Evaluation> evaluationList = getEvaluationList(review, form);
    return evaluationRepository.saveAll(evaluationList);
  }

  public List<Evaluation> getEvaluationList(ProjectReview review, EvaluationForm form) {
    return getAllEvaluations(review, form);
  }

  private List<Evaluation> getAllEvaluations(ProjectReview review,
      EvaluationForm form) {
    return Stream.of(
            EvaluationItem.MANNER,
            EvaluationItem.CONTRIBUTION,
            EvaluationItem.COMMUNICATION,
            EvaluationItem.SCHEDULE,
            EvaluationItem.PROFESSIONALISM
        )
        .map(item -> getEvaluation(review, item, form.getValue(item)))
        .collect(Collectors.toList());
  }

  private Evaluation getEvaluation(ProjectReview review, EvaluationItem evaluationItem, int point) {
    return Evaluation.builder()
        .projectReview(review)
        .evaluationItem(evaluationItem)
        .point(point)
        .build();
  }
}
