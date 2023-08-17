package com.devee.devhive.domain.user.repository;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.UserTechStack;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {
    List<UserTechStack> findAllByUser(User user);

    void deleteAllByUserAndTechStackIdIn(User user, List<Long> techStackIds);
}
