package com.devee.devhive.domain.user.badge.service;

import com.devee.devhive.domain.badge.entity.Badge;
import com.devee.devhive.domain.badge.service.BadgeService;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.devee.devhive.domain.user.badge.entity.UserBadge;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserBadgeRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeService badgeService;

    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findAllByUserId(userId);
    }

    public List<UserBadge> create(User user) {
        List<Badge> badges = badgeService.getAllBadges();

        List<UserBadge> userBadges = badges.stream()
            .map(badge -> UserBadge.builder()
            .user(user)
            .totalScore(0)
            .badge(badge)
            .build()).toList();

        return userBadgeRepository.saveAll(userBadges);
    }

    public void updatePoint(User user, List<Evaluation> evaluationList) {
        List<UserBadge> userBadges = getUserBadges(user.getId());
        // 유저의 첫 리뷰인 경우 유저뱃지 생성
        if (userBadges.isEmpty()) {
            userBadges = create(user);
        }

        // 맵 <평가 항목(뱃지아이디), 점수>
        Map<Long, Integer> badgePointsMap = evaluationList.stream()
            .collect(Collectors.toMap(
                evaluation -> evaluation.getBadge().getId(), Evaluation::getPoint
            ));
        // 유저 뱃지 점수 업데이트
        userBadges.forEach(userBadge -> {
            Long badgeId = userBadge.getBadge().getId();
            badgePointsMap.computeIfPresent(badgeId, (key, value) -> {
                userBadge.setTotalScore(userBadge.getTotalScore() + value);
                return userBadge.getTotalScore();
            });
        });

        userBadgeRepository.saveAll(userBadges);
    }
}
