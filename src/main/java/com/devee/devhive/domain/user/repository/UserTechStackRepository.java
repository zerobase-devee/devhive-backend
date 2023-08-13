package com.devee.devhive.domain.user.repository;

import com.devee.devhive.domain.user.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {

}
