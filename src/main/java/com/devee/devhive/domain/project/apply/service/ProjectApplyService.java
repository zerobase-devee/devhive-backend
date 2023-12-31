package com.devee.devhive.domain.project.apply.service;

import static com.devee.devhive.global.exception.ErrorCode.APPLICATION_ALREADY_ACCEPT;
import static com.devee.devhive.global.exception.ErrorCode.APPLICATION_ALREADY_REJECT;
import static com.devee.devhive.global.exception.ErrorCode.APPLICATION_STATUS_NOT_PENDING;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_APPLICATION;
import static com.devee.devhive.global.exception.ErrorCode.PROJECT_ALREADY_APPLIED;
import static com.devee.devhive.global.exception.ErrorCode.RECRUITMENT_ALREADY_COMPLETED;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.apply.entity.ProjectApply;
import com.devee.devhive.domain.project.apply.repository.ProjectApplyRepository;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.type.ApplyStatus;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.exception.ErrorCode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectApplyService {

  private final ApplicationEventPublisher eventPublisher;
  private final ProjectApplyRepository projectApplyRepository;

  // 신청 상태
  public ApplyStatus getApplicationStatus(User user, Project project) {
    Optional<ProjectApply> projectApplyOptional =
        projectApplyRepository.findByUserIdAndProjectId(user.getId(), project.getId());

    return projectApplyOptional.map(ProjectApply::getStatus).orElse(null);
  }

  public ProjectApply getProjectApplyById(Long applicationId) {
    return projectApplyRepository.findById(applicationId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_APPLICATION));
  }

  public void deleteAll(Long projectId) {
    List<ProjectApply> projectApplies = getProjectApplies(projectId);
    projectApplyRepository.deleteAll(projectApplies);
  }

  // 신청
  @Transactional
  public void projectApplyAndSendAlarmToProjectUser(User user, Project project) {
    // 자기가 작성한 프로젝트에 신청하는 경우
    Long userId = user.getId();
    if (Objects.equals(project.getUser().getId(), userId)) {
      throw new CustomException(UNAUTHORIZED);
    }
    // 프로젝트 모집이 완료된 상태
    ProjectStatus projectStatus = project.getStatus();
    if (projectStatus == ProjectStatus.RECRUITMENT_COMPLETE
        || projectStatus == ProjectStatus.COMPLETE) {
      throw new CustomException(RECRUITMENT_ALREADY_COMPLETED);
    }
    Long projectId = project.getId();
    // 이미 신청한 적이 있는 경우
    projectApplyRepository.findByUserIdAndProjectId(userId, projectId)
        .ifPresent(apply -> {
          ApplyStatus status = apply.getStatus();
          ErrorCode errorMessage = switch (status) {
            case PENDING -> PROJECT_ALREADY_APPLIED;
            case ACCEPT -> APPLICATION_ALREADY_ACCEPT;
            default -> APPLICATION_ALREADY_REJECT;
          };
          throw new CustomException(errorMessage);
        });

    projectApplyRepository.save(ProjectApply.builder()
        .project(project)
        .user(user)
        .status(ApplyStatus.PENDING)
        .build());

    // 프로젝트 작성자에게 신청자 알림 이벤트 발행
    alarmEventPub(project.getUser(), project, AlarmContent.PROJECT_APPLY);
  }

  // 신청 취소
  public void deleteApplication(Long userId, Long projectId) {
    projectApplyRepository.findByUserIdAndProjectId(userId, projectId)
        .ifPresent(projectApply -> {
          // 자신의 프로젝트 신청건이 아닌 경우
          if (!Objects.equals(projectApply.getUser().getId(), userId)) {
            throw new CustomException(UNAUTHORIZED);
          }
          // 신청 상태가 대기상태가 아닌 경우
          if (projectApply.getStatus() != ApplyStatus.PENDING) {
            throw new CustomException(APPLICATION_STATUS_NOT_PENDING);
          }
          projectApplyRepository.delete(projectApply);
        });
  }

  // 프로젝트 신청 목록
  public List<ProjectApply> getProjectApplies(Long projectId) {
    return projectApplyRepository.findAllByProjectId(projectId);
  }

  // 신청 승인
  @Transactional
  public void acceptAndSendAlarmToApplicant(ProjectApply projectApply) {
    // 신청 대기 상태가 아닌 경우 (이미 승인/거절된 경우)
    if (projectApply.getStatus() != ApplyStatus.PENDING) {
      throw new CustomException(APPLICATION_STATUS_NOT_PENDING);
    }
    projectApply.setStatus(ApplyStatus.ACCEPT);
    projectApplyRepository.save(projectApply);

    // 프로젝트 신청자에게 승인 알림 이벤트 발행
    alarmEventPub(projectApply.getUser(), projectApply.getProject(), AlarmContent.APPLICANT_ACCEPT);
  }

  // 신청 거절
  @Transactional
  public void rejectAndSendAlarmToApplicant(User user, Long applicationId) {
    ProjectApply projectApply = getProjectApplyById(applicationId);
    // 프로젝트 작성자가 아닌 경우
    if (!Objects.equals(projectApply.getProject().getUser().getId(), user.getId())) {
      throw new CustomException(UNAUTHORIZED);
    }
    // 신청 대기 상태가 아닌 경우 (이미 승인/거절된 경우)
    if (projectApply.getStatus() != ApplyStatus.PENDING) {
      throw new CustomException(APPLICATION_STATUS_NOT_PENDING);
    }

    projectApply.setStatus(ApplyStatus.REJECT);
    projectApplyRepository.save(projectApply);

    // 프로젝트 신청자에게 거절 알림 이벤트 발행
    alarmEventPub(projectApply.getUser(), projectApply.getProject(), AlarmContent.APPLICANT_REJECT);
  }

  private void alarmEventPub(User user, Project project, AlarmContent content) {
    AlarmForm alarmForm = AlarmForm.builder()
        .receiverUser(user)
        .projectId(project.getId())
        .projectName(project.getName())
        .content(content)
        .build();
    eventPublisher.publishEvent(alarmForm);
  }
}
