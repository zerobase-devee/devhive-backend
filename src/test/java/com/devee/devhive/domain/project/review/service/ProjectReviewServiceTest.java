package com.devee.devhive.domain.project.review.service;

import static com.devee.devhive.global.exception.ErrorCode.PROJECT_NOT_COMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.repository.ProjectReviewRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProjectReviewServiceTest {

  @InjectMocks
  private ProjectReviewService projectReviewService;
  @Mock
  private ProjectReviewRepository projectReviewRepository;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("리뷰 생성 - 성공")
  void testSubmitReview() {
    // given
    User user = User.builder().id(1L).build();
    User targetUser = User.builder().id(2L).build();

    Project project = Project.builder()
        .id(1L)
        .status(ProjectStatus.COMPLETE)
        .build();

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

    when(projectReviewRepository.saveAndFlush(any(ProjectReview.class)))
        .thenReturn(projectReview);

    // when
    ProjectReview newReview = projectReviewService.submitReview(project.getId(), targetUser.getId(),
        user, project, targetUser, form);

    // then
    assertThat(newReview).isNotNull();
    assertThat(newReview.getTotalScore()).isEqualTo(25);
  }

  @Test
  @DisplayName("리뷰 생성 - 실패_프로젝트가 완료 상태가 아님")
  void testSubmitReview_NotComplete() {
    // given
    Project project = Project.builder()
        .id(1L)
        .status(ProjectStatus.RECRUITMENT_COMPLETE)
        .build();

    User user = User.builder().id(1L).build();
    User targetUser = User.builder().id(2L).build();

    EvaluationForm form = EvaluationForm.builder()
        .build();

    // when
    CustomException exception = assertThrows(CustomException.class,
        () -> projectReviewService.submitReview(project.getId(), targetUser.getId(),
            user, project, targetUser, form));

    // then
    assertEquals(PROJECT_NOT_COMPLETE, exception.getErrorCode());
  }
}