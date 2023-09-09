package com.devee.devhive.domain.project.member.service;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.repository.ProjectMemberRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.AlarmContent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

  private final ApplicationEventPublisher eventPublisher;
  private final ProjectMemberRepository projectMemberRepository;

  public List<ProjectMember> getProjectMemberByProjectId(Long projectId) {
    return projectMemberRepository.findAllByProjectIdOrderByCreatedDateAsc(projectId);
  }

  // 유저가 참여한 완료된 프로젝트 갯수 (벌집레벨)
  public int countCompletedProjectsByUserId(Long userId) {
    List<ProjectMember> projectMembers = projectMemberRepository.findAllByUserIdOrderByCreatedDateDesc(userId);
    long completedProjectCount = projectMembers.stream()
        .map(ProjectMember::getProject)
        .filter(project -> project.getStatus() == ProjectStatus.COMPLETE)
        .count();

    return (int) completedProjectCount;
  }

  // 유저가 참여한 프로젝트 목록(최신순)
  public List<ProjectMember> findAllByUserId(Long userId) {
    return projectMemberRepository.findAllByUserIdOrderByCreatedDateDesc(userId);
  }

  // 내가 참여한 프로젝트 목록 페이지
  public Page<ProjectMember> getParticipationProjects(Long userId, Pageable pageable) {
    return projectMemberRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
  }

  // 참가인원 수
  public int countAllByProjectId(Long projectId) {
    return projectMemberRepository.countAllByProjectId(projectId);
  }

  // 신청 승인된 유저 멤버 저장
  public void saveProjectMember(User user, Project project) {
    projectMemberRepository.save(ProjectMember.builder()
        .user(user)
        .project(project)
        .leader(false)
        .build());
  }

  // 프로젝트 작성자 리더 저장
  public void saveProjectLeader(User user, Project project) {
    projectMemberRepository.save(ProjectMember.builder()
        .user(user)
        .project(project)
        .leader(true)
        .build());
  }

  public void deleteProjectMembers(Long projectId) {
    List<ProjectMember> projectMembers = getProjectMemberByProjectId(projectId);
    projectMemberRepository.deleteAll(projectMembers);
  }

  // 해당 프로젝트에 유저가 참가해있는지 체크
  public boolean isMemberofProject(Long projectId, Long userId) {
    return projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
  }
  
  @Transactional
  public void deleteAllOfMembersFromProjectAndSendAlarm(Long projectId) {
    List<ProjectMember> projectMembers = getProjectMemberByProjectId(projectId);
    projectMemberRepository.deleteAll(projectMembers);

    // 리더가 퇴출된 경우 프로젝트 멤버들에게 프로젝트 삭제 알림 이벤트 발행
    for (ProjectMember projectMember : projectMembers) {
      alarmEventPub(projectMember.getUser(), projectMember.getProject(),
          AlarmContent.EXIT_LEADER_DELETE_PROJECT, null);
    }
  }

  @Transactional
  public void deleteMemberFromProjectAndSendAlarm(Long projectId, Long userId) {
    ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
    List<ProjectMember> projectMembers = getProjectMemberByProjectId(projectId);

    projectMemberRepository.delete(projectMember);

    // 프로젝트 멤버들에게 퇴출자 알림 이벤트 발행
    for (ProjectMember member : projectMembers) {
      alarmEventPub(member.getUser(), member.getProject(),
          AlarmContent.VOTE_RESULT_EXIT_SUCCESS, projectMember.getUser());
    }
  }

  private void alarmEventPub(User receiver, Project project, AlarmContent content, User user) {
    AlarmForm alarmForm = AlarmForm.builder()
        .receiverUser(receiver)
        .project(project)
        .content(content)
        .user(user)
        .build();
    eventPublisher.publishEvent(alarmForm);
  }
}
