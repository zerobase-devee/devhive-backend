package com.devee.devhive.domain.project.review.evaluation.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.devee.devhive.domain.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.repository.EvaluationRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
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

    List<EvaluationForm> forms = List.of(
        EvaluationForm.builder()
            .badgeDto(BadgeDto.builder().id(1L).name("매너").image("매너 url").build())
            .point(3)
            .build(),
        EvaluationForm.builder()
            .badgeDto(BadgeDto.builder().id(2L).name("척척박사").image("척척박사 url").build())
            .point(4)
            .build());

    ProjectReview projectReview = ProjectReview.builder()
        .id(1L)
        .reviewerUser(user)
        .targetUser(targetUser)
        .project(Project.builder()
            .id(1L)
            .status(ProjectStatus.RECRUITMENT_COMPLETE).build())
        .totalScore(7)
        .build();

    // when
    evaluationService.saveAllEvaluations(projectReview, forms);

    // then
    verify(evaluationRepository, times(1)).saveAll(anyList());
  }
}