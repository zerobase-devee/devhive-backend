package com.devee.devhive.api.user.repository;

import com.devee.devhive.api.user.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

}
