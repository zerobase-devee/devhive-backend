package com.devee.devhive.domain.user.exithistory.repository.impl;

import com.devee.devhive.domain.user.exithistory.entity.QExitHistory;
import com.devee.devhive.domain.user.exithistory.repository.custom.CustomExitHistoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomExitHistoryRepositoryImpl implements CustomExitHistoryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Long> getReactivatingUsers() {

    QExitHistory qExitHistory = QExitHistory.exitHistory;

    return queryFactory.select(qExitHistory.user.id, qExitHistory.reActiveDate.max())
        .from(qExitHistory)
        .where(qExitHistory.reActiveDate.before(Instant.now()))
        .groupBy(QExitHistory.exitHistory.user.id)
        .fetch()
        .stream()
        .map(t -> t.get(qExitHistory.user.id))
        .collect(Collectors.toList());
  }
}
