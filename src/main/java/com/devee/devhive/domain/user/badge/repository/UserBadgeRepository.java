package com.devee.devhive.domain.user.badge.repository;

import com.devee.devhive.domain.user.badge.entity.UserBadge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findAllByUserId(Long userId);
}
