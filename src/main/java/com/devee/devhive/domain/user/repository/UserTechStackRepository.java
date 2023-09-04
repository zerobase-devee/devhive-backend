package com.devee.devhive.domain.user.repository;

import com.devee.devhive.domain.user.techstack.entity.UserTechStack;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {
    List<UserTechStack> findAllByUserId(Long userId);

    void deleteAllByUserIdAndTechStackIdIn(Long userId, List<Long> techStackIds);

    List<UserTechStack> findAllByTechStackIdIn(List<Long> techStackIds);
}
