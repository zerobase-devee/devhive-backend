package com.devee.devhive.domain.project.service;

import static com.devee.devhive.domain.project.type.DevelopmentType.BACKEND;
import static com.devee.devhive.domain.project.type.DevelopmentType.FRONTEND;
import static com.devee.devhive.domain.project.type.ProjectStatus.RECRUITING;
import static com.devee.devhive.domain.project.type.RecruitmentType.OFFLINE;
import static com.devee.devhive.domain.project.type.RecruitmentType.ONLINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.dto.CreateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectStatusDto;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProjectServiceTest {

  @InjectMocks
  private ProjectService projectService;
  @Mock
  private ProjectRepository projectRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("프로젝트 생성 - 성공_오프라인")
  void testCreateProject_Offline() {
    // Given
    User user = User.builder().id(1L).build();

    CreateProjectDto createProjectDto = CreateProjectDto.builder()
        .title("Project")
        .projectName("Project Name")
        .content("content")
        .teamSize(5)
        .recruitmentType(OFFLINE)
        .developmentType(BACKEND)
        .deadline(LocalDateTime.now().plusMonths(1))
        .region("Region")
        .build();

    Project project = Project.builder()
        .user(user)
        .title(createProjectDto.getTitle())
        .name(createProjectDto.getProjectName())
        .content(createProjectDto.getContent())
        .teamSize(createProjectDto.getTeamSize())
        .recruitmentType(OFFLINE)
        .developmentType(createProjectDto.getDevelopmentType())
        .deadline(createProjectDto.getDeadline())
        .status(RECRUITING)
        .region(createProjectDto.getRegion())
        .build();

    when(projectRepository.save(any(Project.class))).thenReturn(project);

    // When
    Project createdProject = projectService.createProject(createProjectDto, user);

    // Then
    assertThat(createdProject).isNotNull();
    assertThat(createdProject.getStartDate()).isNull();
    assertThat(createdProject.getEndDate()).isNull();
    assertThat(createdProject).isEqualTo(project);
  }

  @Test
  @DisplayName("프로젝트 생성 - 성공_온라인")
  void testCreateProject_Online() {
    // Given
    User user = User.builder().id(1L).build();

    CreateProjectDto createProjectDto = CreateProjectDto.builder()
        .title("Test Project")
        .projectName("Test Project Name")
        .content("Test content")
        .teamSize(5)
        .recruitmentType(ONLINE)
        .developmentType(BACKEND)
        .deadline(LocalDateTime.now().plusMonths(1))
        .build();

    Project project = Project.builder()
        .id(1L)
        .user(user)
        .title(createProjectDto.getTitle())
        .name(createProjectDto.getProjectName())
        .content(createProjectDto.getContent())
        .teamSize(createProjectDto.getTeamSize())
        .recruitmentType(ONLINE)
        .developmentType(createProjectDto.getDevelopmentType())
        .deadline(createProjectDto.getDeadline())
        .status(RECRUITING)
        .build();

    when(projectRepository.save(any(Project.class))).thenReturn(project);

    // When
    Project createdProject = projectService.createProject(createProjectDto, user);

    // Then
    assertThat(createdProject).isNotNull();
    assertThat(createdProject.getRegion()).isNull();
    assertThat(createdProject.getStartDate()).isNull();
    assertThat(createdProject.getEndDate()).isNull();
    assertThat(createdProject).isEqualTo(project);
  }

  @Test
  @DisplayName("프로젝트 상태 변경 - 성공_모집 완료")
  void testUpdateProjectStatus_UpdatesStatus() {
    // Given
    User user = User.builder().id(1L).build();
    Project project = Project.builder()
        .id(1L)
        .user(user)
        .status(RECRUITING)
        .build();

    UpdateProjectStatusDto statusDto = new UpdateProjectStatusDto();
    statusDto.setStatus(ProjectStatus.RECRUITMENT_COMPLETE);

    List<ProjectMember> members = new ArrayList<>();

    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
    when(projectRepository.save(any(Project.class))).thenReturn(project);

    // When
    projectService.updateProjectStatus(user, project.getId(), statusDto, members);

    // Then
    verify(projectRepository, times(1)).findById(project.getId());
    verify(projectRepository, times(1)).save(project);

    assertThat(project.getStartDate()).isNotNull();
    assertThat(project.getEndDate()).isNull();
  }

  @Test
  @DisplayName("프로젝트 상태 변경 - 실패_글 작성자가 아님")
  void testUpdateProjectStatus_InvalidUser() {
    // Given
    User user = User.builder().id(1L).build();
    User otherUser = User.builder().id(2L).build();

    Project project = Project.builder()
        .id(1L)
        .user(otherUser)
        .build();

    UpdateProjectStatusDto statusDto = new UpdateProjectStatusDto();
    statusDto.setStatus(ProjectStatus.RECRUITMENT_COMPLETE);

    List<ProjectMember> members = new ArrayList<>();

    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

    // When
    assertThrows(CustomException.class,
        () -> projectService.updateProjectStatus(user, project.getId(), statusDto, members));

    // Then
    verify(projectRepository, times(1)).findById(project.getId());
    verify(projectRepository, never()).save(any(Project.class));
  }

  @Test
  @DisplayName("프로젝트 수정 - 성공")
  void testUpdateProject() {
    // Given
    User user = User.builder().id(1L).build();

    UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
        .title("New Title")
        .projectName("New Project Name")
        .teamSize(10)
        .recruitmentType(OFFLINE)
        .developmentType(BACKEND)
        .deadline(LocalDateTime.now().plusMonths(2))
        .region("New Region")
        .build();

    Project existingProject = Project.builder()
        .id(1L)
        .user(user)
        .title("Title")
        .name("Project Name")
        .teamSize(5)
        .recruitmentType(ONLINE)
        .developmentType(FRONTEND)
        .deadline(LocalDateTime.now().plusMonths(1))
        .build();

    when(projectRepository.findById(existingProject.getId())).thenReturn(
        Optional.of(existingProject));
    when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

    // When
    Project updatedProject = projectService.updateProject(user, existingProject.getId(),
        updateProjectDto);

    // Then
    assertThat(updatedProject.getTitle()).isEqualTo(updateProjectDto.getTitle());
    assertThat(updatedProject.getName()).isEqualTo(updateProjectDto.getProjectName());
    assertThat(updatedProject.getTeamSize()).isEqualTo(updateProjectDto.getTeamSize());
    assertThat(updatedProject.getRecruitmentType()).isEqualTo(
        updateProjectDto.getRecruitmentType());
    assertThat(updatedProject.getDevelopmentType()).isEqualTo(
        updateProjectDto.getDevelopmentType());
    assertThat(updatedProject.getDeadline()).isEqualTo(updateProjectDto.getDeadline());

    if (updateProjectDto.getRecruitmentType() == OFFLINE) {
      assertThat(updatedProject.getRegion()).isEqualTo(updateProjectDto.getRegion());
    }

    verify(projectRepository, times(1)).save(updatedProject);
  }

  @Test
  @DisplayName("프로젝트 수정 - 실패_글 작성자가 아님")
  public void testUpdateProject_InvalidUser() {
    // Given
    User user = User.builder().id(1L).build();
    User otherUser = User.builder().id(2L).build();

    UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
        .title("New Title")
        .projectName("New Project Name")
        .teamSize(10)
        .recruitmentType(OFFLINE)
        .developmentType(BACKEND)
        .deadline(LocalDateTime.now().plusMonths(2))
        .region("New Region")
        .build();

    Project project = Project.builder()
        .id(1L)
        .user(user)
        .build();

    when(projectRepository.findById(project.getId())).thenReturn(
        Optional.of(project));

    // When
    assertThrows(CustomException.class,
        () -> projectService.updateProject(otherUser, project.getId(), updateProjectDto));

    // Then
    verify(projectRepository, never()).save(any(Project.class));
  }

  @Test
  @DisplayName("프로젝트 삭제 - 성공")
  public void testDeleteProject() {
    // Given
    Project project = Project.builder()
        .id(1L)
        .status(ProjectStatus.RECRUITING)
        .build();

    when(projectRepository.findById(project.getId())).thenReturn(
        Optional.of(project));

    // When
    assertDoesNotThrow(() -> projectService.deleteProject(project));

    // Then
    verify(projectRepository, times(1)).delete(project);
  }
}
