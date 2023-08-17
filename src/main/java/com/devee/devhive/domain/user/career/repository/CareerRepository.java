package com.devee.devhive.domain.user.career.repository;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.career.entity.Career;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findAllByUserOrderByStartDateAsc(User user);
}
