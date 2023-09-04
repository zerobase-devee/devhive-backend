package com.devee.devhive.domain.user.badge.service;

import com.devee.devhive.domain.user.badge.entity.UserBadge;
import com.devee.devhive.domain.user.repository.UserBadgeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;

    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findAllByUserId(userId);
    }
}
