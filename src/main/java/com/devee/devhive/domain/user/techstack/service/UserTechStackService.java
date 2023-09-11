package com.devee.devhive.domain.user.techstack.service;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.type.RecruitmentType;
import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.techstack.service.TechStackService;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.techstack.entity.UserTechStack;
import com.devee.devhive.domain.user.techstack.repository.UserTechStackRepository;
import com.devee.devhive.domain.user.type.AlarmContent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTechStackService {

  private final ApplicationEventPublisher eventPublisher;

  private final UserTechStackRepository userTechStackRepository;
  private final TechStackService techStackService;

  public List<UserTechStack> getUserTechStacks(Long userId) {
    return userTechStackRepository.findAllByUserId(userId);
  }

  public List<UserTechStack> findUsersWithTechStacks(List<Long> techStackIds) {
    return userTechStackRepository.findAllByTechStackIdIn(techStackIds);
  }

  @Transactional
  public void recommendAlarmOfProject(Project project, List<TechStackDto> techStacks) {
    List<Long> techStackIds = techStacks.stream().map(TechStackDto::getId).toList();
    // 프로젝트에 등록된 기술을 포함하고 있는 유저 목록
    List<UserTechStack> usersWithTechStacks = findUsersWithTechStacks(techStackIds);

    for (UserTechStack userTechStack : usersWithTechStacks) {
      User user = userTechStack.getUser();
      if (project.getRecruitmentType() == RecruitmentType.ONLINE) {
        recommendAlarmEventPub(user, project);
      } else {
        // 프로젝트가 오프라인이면 지역이 일치하는 유저들에게만 알림 이벤트 발행
        if (project.getRegion().equals(user.getRegion())) {
          recommendAlarmEventPub(user, project);
        }
      }
    }
  }

  private void recommendAlarmEventPub(User receiver, Project project) {
    AlarmForm alarmForm = AlarmForm.builder()
        .receiverUser(receiver)
        .projectId(project.getId())
        .projectName(project.getName())
        .content(AlarmContent.RECOMMEND)
        .build();
    eventPublisher.publishEvent(alarmForm);
  }

  @Transactional
  public void updateTechStacks(User user, List<TechStackDto> newTechStacks) {
    List<UserTechStack> existingTechStacks = getUserTechStacks(user.getId());

    // 기존 유저기술스택이 비었다면 요청된 기술스택 바로 저장
    if (existingTechStacks.isEmpty()) {
      List<UserTechStack> newUserTechStacks = newTechStacks.stream()
          .map(techStackDto -> UserTechStack.of(user, TechStack.from(techStackDto)))
          .collect(Collectors.toList());
      userTechStackRepository.saveAll(newUserTechStacks);
    } else if (newTechStacks.isEmpty()) {
      // 요청된 기술스택이 비었다면 기존 유저기술스택 모두 삭제
      userTechStackRepository.deleteAll(existingTechStacks);
    } else {
      // 기존 유저기술스택 삭제할거 삭제, 추가할거 추가 저장
      List<Long> newTechStackIds = newTechStacks.stream()
          .map(TechStackDto::getId)
          .collect(Collectors.toList());
      List<Long> techStackIdsToDelete = new ArrayList<>();

      for (UserTechStack userTechStack : existingTechStacks) {
        Long curExistingId = userTechStack.getTechStack().getId();
        if (newTechStackIds.contains(curExistingId)) {
          newTechStackIds.remove(curExistingId);
        } else {
          techStackIdsToDelete.add(curExistingId);
        }
      }

      if (!techStackIdsToDelete.isEmpty()) {
        userTechStackRepository.deleteAllByUserIdAndTechStackIdIn(user.getId(), techStackIdsToDelete);
      }

      if (!newTechStackIds.isEmpty()) {
        List<TechStack> techStacks = techStackService.findAllById(newTechStackIds);
        for (TechStack techStack : techStacks) {
          userTechStackRepository.save(UserTechStack.of(user, techStack));
        }
      }
    }
  }
}
