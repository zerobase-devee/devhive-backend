package com.devee.devhive.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.dto.MyProjectInfoDto;
import com.devee.devhive.domain.project.entity.dto.SimpleProjectDto;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.repository.ProjectMemberRepository;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.domain.project.review.repository.ProjectReviewRepository;
import com.devee.devhive.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@SpringBootTest
class UserProjectServiceTest {

    @InjectMocks
    private UserProjectService userProjectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private ProjectReviewRepository projectReviewRepository;

    @Test
    @DisplayName("내가 생성한 프로젝트 목록 조회")
    void testGetWriteProjects() {
        //given
        User user = new User();
        Pageable pageable = Pageable.ofSize(10);

        when(projectRepository.findByWriterUserOrderByCreatedDateDesc(eq(user), eq(pageable)))
            .thenReturn(Page.empty());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        //when
        Page<SimpleProjectDto> projects = userProjectService.getWriteProjects(authentication, pageable);
        //then
        assertTrue(projects.isEmpty());
    }

    @Test
    @DisplayName("내가 참여한 프로젝트 목록 조회")
    void testGetParticipationProjects() {
        //given
        User user = new User();
        Pageable pageable = Pageable.ofSize(10);

        when(projectMemberRepository.findByUserOrderByCreatedDateDesc(eq(user), eq(pageable)))
            .thenReturn(Page.empty());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        //when
        Page<SimpleProjectDto> projects = userProjectService.getParticipationProjects(authentication, pageable);
        //then
        assertTrue(projects.isEmpty());
    }

    @Test
    @DisplayName("내 프로젝트 정보 조회")
    void testGetProjectInfo() {
        // given
        User user = new User();
        Long projectId = 1L;

        Project project = Project.builder().id(projectId).build();
        List<ProjectMember> projectMembers = List.of(
            ProjectMember.builder()
                .project(project)
                .user(user)
                .build()
        );
        project.setProjectMembers(projectMembers);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(projectReviewRepository.getTotalScoreByUserAndProject(eq(user), eq(project))).thenReturn(50);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        // when
        MyProjectInfoDto projectInfo = userProjectService.getProjectInfo(projectId, authentication);
        // then
        assertEquals(10.0, projectInfo.getTotalAverageScore());
    }
}