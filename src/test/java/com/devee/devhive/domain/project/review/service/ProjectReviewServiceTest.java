package com.devee.devhive.domain.project.review.service;

import static com.devee.devhive.global.exception.ErrorCode.PROJECT_NOT_COMPLETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.repository.ProjectReviewRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import java.util.List;
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
    List<EvaluationForm> forms = List.of(
        EvaluationForm.builder()
            .badgeDto(BadgeDto.builder().id(1L).build())
            .point(3)
            .build(),
        EvaluationForm.builder()
            .badgeDto(BadgeDto.builder().id(2L).build())
            .point(4)
            .build());

    when(projectReviewRepository.existsByProjectIdAndReviewerUserIdAndTargetUserId(project.getId(),user.getId(), targetUser.getId()))
        .thenReturn(false);

    // when
    projectReviewService.submitReview(
        project.getId(), targetUser.getId(), user, project, targetUser, forms);

    // then
    verify(projectReviewRepository, times(1)).save(any(ProjectReview.class));
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
        () -> projectReviewService.submitReview(project.getId(),
            targetUser.getId(),
            user, project, targetUser, List.of(form)));

    // then
    assertEquals(PROJECT_NOT_COMPLETE, exception.getErrorCode());
  }
}