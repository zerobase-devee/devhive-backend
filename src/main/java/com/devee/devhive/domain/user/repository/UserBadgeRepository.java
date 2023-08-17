package com.devee.devhive.domain.user.repository;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.UserBadge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findAllByUser(User user);
}
