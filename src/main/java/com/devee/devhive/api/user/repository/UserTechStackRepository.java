package com.devee.devhive.api.user.repository;

import com.devee.devhive.api.user.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {

}
