package com.devee.devhive.domain.project.review.evaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.devee.devhive.domain.project.review.evaluation.repository.EvaluationRepository;
import com.devee.devhive.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EvaluationServiceTest {

  @InjectMocks
  private EvaluationService evaluationService;
  @Mock
  private EvaluationRepository evaluationRepository;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("평가항목 저장 - 성공")
  void testSubmitReview() {
    // given
    User user = User.builder().id(1L).build();
    User targetUser = User.builder().id(2L).build();

    EvaluationForm form = EvaluationForm.builder()
        .manner(5)
        .contribution(5)
        .communication(5)
        .schedule(5)
        .professionalism(5)
        .build();

    ProjectReview projectReview = ProjectReview.builder()
        .id(1L)
        .reviewerUser(user)
        .targetUser(targetUser)
        .totalScore(form.getTotalScore())
        .build();

    List<Evaluation> evaluationList = evaluationService.getEvaluationList(projectReview, form);

    when(evaluationRepository.saveAll(any()))
        .thenReturn(evaluationList);

    // when
    List<Evaluation> savedEvaluationList = evaluationService.saveAllEvaluations(projectReview,form);

    // then
    assertThat(savedEvaluationList).isNotNull();
    assertThat(savedEvaluationList.size()).isEqualTo(5);
  }
}