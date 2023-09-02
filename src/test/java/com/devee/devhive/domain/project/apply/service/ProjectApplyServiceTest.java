package com.devee.devhive.domain.project.apply.service;

import static com.devee.devhive.global.exception.ErrorCode.APPLICATION_STATUS_NOT_PENDING;
import static com.devee.devhive.global.exception.ErrorCode.PROJECT_ALREADY_APPLIED;
import static com.devee.devhive.global.exception.ErrorCode.RECRUITMENT_ALREADY_COMPLETED;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.apply.entity.ProjectApply;
import com.devee.devhive.domain.project.apply.repository.ProjectApplyRepository;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.type.ApplyStatus;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

class ProjectApplyServiceTest {
  @InjectMocks
  private ProjectApplyService projectApplyService;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private ProjectApplyRepository projectApplyRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("신청 - 성공")
  void testProjectApply() {
    // Given
    User user = User.builder().id(1L).build();
    User projectUser = User.builder().id(2L).build();
    Project project = Project.builder()
        .id(1L)
        .user(projectUser)
        .status(ProjectStatus.RECRUITING)
        .build();

    when(projectApplyRepository.findByUserIdAndProjectId(user.getId(), project.getId())).thenReturn(Optional.empty());
    // When
    projectApplyService.projectApplyAndSendAlarmToProjectUser(user, project);

    // Then
    verify(projectApplyRepository, times(1)).save(any(ProjectApply.class));
    verify(eventPublisher, times(1)).publishEvent(any(AlarmForm.class));
  }

  @Test
  @DisplayName("신청 - 실패_작성자인 경우")
  void testProjectApply_Fail_UNAUTHORIZED() {
    // Given
    User user = User.builder().id(1L).build();
    Project project = Project.builder()
        .id(1L)
        .user(user)
        .status(ProjectStatus.RECRUITING)
        .build();

    // When
    CustomException exception = assertThrows(CustomException.class,
        () -> projectApplyService.projectApplyAndSendAlarmToProjectUser(user, project));

    // then
    assertEquals(UNAUTHORIZED, exception.getErrorCode());
  }

  @Test
  @DisplayName("신청 - 실패_모집중이 아님")
  void testProjectApply_Fail_NOT_RECRUITING() {
    // Given
    User user = User.builder().id(1L).build();
    User projectUser = User.builder().id(2L).build();
    Project project = Project.builder()
        .id(1L)
        .user(projectUser)
        .status(ProjectStatus.RECRUITMENT_COMPLETE)
        .build();

    // When
    CustomException exception = assertThrows(CustomException.class,
        () -> projectApplyService.projectApplyAndSendAlarmToProjectUser(user, project));

    // then
    assertEquals(RECRUITMENT_ALREADY_COMPLETED, exception.getErrorCode());
  }

  @Test
  @DisplayName("신청 - 실패_이미 신청 상태")
  void testProjectApply_Fail_PROJECT_ALREADY_APPLIED() {
    // Given
    User user = User.builder().id(1L).build();
    User projectUser = User.builder().id(2L).build();
    Project project = Project.builder()
        .id(1L)
        .user(projectUser)
        .status(ProjectStatus.RECRUITING)
        .build();

    ProjectApply projectApply = ProjectApply.builder()
        .id(1L)
        .user(user)
        .project(project)
        .status(ApplyStatus.PENDING)
        .build();

    when(projectApplyRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
        .thenReturn(Optional.of(projectApply));
    // When
    CustomException exception = assertThrows(CustomException.class,
        () -> projectApplyService.projectApplyAndSendAlarmToProjectUser(user, project));

    // then
    assertEquals(PROJECT_ALREADY_APPLIED, exception.getErrorCode());
  }

  @Test
  @DisplayName("신청 취소 - 성공")
  void testDeleteApplication() {
    // Given
    Long userId = 1L;
    Long projectId = 1L;
    ProjectApply projectApply = ProjectApply.builder()
        .id(1L)
        .user(User.builder().id(userId).build())
        .status(ApplyStatus.PENDING)
        .build();

    when(projectApplyRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(projectApply));
    // When
    projectApplyService.deleteApplication(userId, projectId);

    // Then
    verify(projectApplyRepository, times(1)).delete(projectApply);
  }

  @Test
  @DisplayName("신청 취소 - 실패_이미 승인/거절됨")
  void testDeleteApplication_Fail_APPLICATION_STATUS_NOT_PENDING() {
    // Given
    Long userId = 1L;
    Long projectId = 1L;
    ProjectApply projectApply = ProjectApply.builder()
        .id(1L)
        .user(User.builder().id(userId).build())
        .status(ApplyStatus.ACCEPT)
        .build();

    when(projectApplyRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(projectApply));
    // When
    CustomException exception = assertThrows(CustomException.class,
        () -> projectApplyService.deleteApplication(userId, projectId));

    // then
    assertEquals(APPLICATION_STATUS_NOT_PENDING, exception.getErrorCode());
  }

  @Test
  @DisplayName("신청 승인 - 성공")
  void testAccept() {
    // Given
    ProjectApply projectApply = ProjectApply.builder()
        .id(1L)
        .status(ApplyStatus.PENDING)
        .build();

    // When
    projectApplyService.acceptAndSendAlarmToApplicant(projectApply);

    // Then
    assertEquals(ApplyStatus.ACCEPT, projectApply.getStatus());
    verify(eventPublisher, times(1)).publishEvent(any(AlarmForm.class));
  }

  @Test
  @DisplayName("신청 거절 - 성공")
  void testReject() {
    // Given
    User user = User.builder().id(2L).build();
    ProjectApply projectApply = ProjectApply.builder()
        .id(1L)
        .user(User.builder().id(1L).build())
        .status(ApplyStatus.PENDING)
        .project(Project.builder().user(user).build())
        .build();

    when(projectApplyRepository.findById(projectApply.getId())).thenReturn(Optional.of(projectApply));
    when(projectApplyRepository.save(any(ProjectApply.class))).thenReturn(projectApply);
    // When
    projectApplyService.rejectAndSendAlarmToApplicant(user, projectApply.getId());

    // Then
    assertEquals(ApplyStatus.REJECT, projectApply.getStatus());
    verify(eventPublisher, times(1)).publishEvent(any(AlarmForm.class));
  }
}