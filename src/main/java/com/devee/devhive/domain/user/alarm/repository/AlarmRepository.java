package com.devee.devhive.domain.user.alarm.repository;

import com.devee.devhive.domain.user.alarm.entity.Alarm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findAllByUserId(Long userId);
}
