package com.devee.devhive.domain.user.service;

import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.techstack.service.TechStackService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.UserTechStack;
import com.devee.devhive.domain.user.repository.UserTechStackRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTechStackService {

    private final UserTechStackRepository userTechStackRepository;
    private final TechStackService techStackService;

    public List<UserTechStack> getUserTechStacks(Long userId) {
        return userTechStackRepository.findAllByUserId(userId);
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
        }else {
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
                userTechStackRepository.deleteAllByUserIdAndTechStackIdIn(
                    user.getId(), techStackIdsToDelete);
            }

            if (!newTechStackIds.isEmpty()) {
                List<TechStack> techStacks = techStackService.findAllById(newTechStackIds);
                for (TechStack techStack : techStacks) {
                    userTechStackRepository.save(
                        UserTechStack.of(user, techStack));
                }
            }
        }
    }
}
