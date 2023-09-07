package com.devee.devhive.domain.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.type.RecruitmentType;
import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.techstack.service.TechStackService;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.techstack.entity.UserTechStack;
import com.devee.devhive.domain.user.techstack.repository.UserTechStackRepository;
import com.devee.devhive.domain.user.techstack.service.UserTechStackService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

class UserTechStackServiceTest {
  @InjectMocks
  private UserTechStackService userTechStackService;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private TechStackService techStackService;
  @Mock
  private UserTechStackRepository userTechStackRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("프로젝트 추천 알림 - 성공")
  void testRecommendProject() {
    // Given
    // 프로젝트가 가진 기술
    List<TechStackDto> techStacks = Arrays.asList(
        TechStackDto.builder()
        .id(1L)
        .build(),
        TechStackDto.builder()
        .id(3L)
        .build());

    Project project = new Project();
    project.setRecruitmentType(RecruitmentType.OFFLINE);
    project.setRegion("seoul");

    User user = User.builder().region("seoul").build();
    User user2 = User.builder().region("BuSan").build();

    List<UserTechStack> usersWithTechStacks = Arrays.asList(
        UserTechStack.builder()
            .user(user)
            .techStack(TechStack.builder().id(1L).build())
            .build(),
        UserTechStack.builder()
            .user(user2)
            .techStack(TechStack.builder().id(3L).build())
            .build()
    );
    when(userTechStackRepository.findAllByTechStackIdIn(
        techStacks.stream().map(TechStackDto::getId).toList())
    ).thenReturn(usersWithTechStacks);

    // When
    userTechStackService.recommendAlarmOfProject(project, techStacks);

    // Then
    verify(eventPublisher, times(1)).publishEvent(any(AlarmForm.class));
  }

  @Test
  @DisplayName("유저 기술스택 업데이트 - 성공")
  void testUpdateTechStacks() {
    // Given
    User user = User.builder().id(1L).build();

    TechStackDto techStackDto = TechStackDto.builder()
        .id(2L)
        .build();
    List<TechStackDto> newTechStacks = List.of(techStackDto);
    List<Long> newTechStackIds = List.of(2L);
    List<TechStack> techStacks = List.of(TechStack.builder().id(2L).build());

    List<UserTechStack> existingTechStacks = new ArrayList<>();
    UserTechStack existingUserTechStack = UserTechStack.builder()
            .techStack(TechStack.builder().id(5L).build())
            .user(user)
            .build();
    existingTechStacks.add(existingUserTechStack);

    when(userTechStackRepository.findAllByUserId(user.getId())).thenReturn(existingTechStacks);
    when(techStackService.findAllById(newTechStackIds)).thenReturn(techStacks);

    // When
    userTechStackService.updateTechStacks(user, newTechStacks);

    // Then
    verify(userTechStackRepository, times(1)).deleteAllByUserIdAndTechStackIdIn(user.getId(), List.of(5L));
    verify(userTechStackRepository, times(1)).save(any(UserTechStack.class));
  }
}