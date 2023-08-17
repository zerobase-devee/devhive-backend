package com.devee.devhive.domain.user.exithistory.repository;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.exithistory.entity.ExitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExitHistoryRepository extends JpaRepository<ExitHistory, Long> {
    int countExitHistoryByUser(User user);
}
