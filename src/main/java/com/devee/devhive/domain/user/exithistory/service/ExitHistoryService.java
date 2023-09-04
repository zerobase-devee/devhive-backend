package com.devee.devhive.domain.user.exithistory.service;

import com.devee.devhive.domain.user.exithistory.entity.ExitHistory;
import com.devee.devhive.domain.user.exithistory.repository.ExitHistoryRepository;
import com.devee.devhive.domain.user.exithistory.repository.impl.CustomExitHistoryRepositoryImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExitHistoryService {

  private final ExitHistoryRepository exitHistoryRepository;
  private final CustomExitHistoryRepositoryImpl customExitHistoryRepository;

  // 퇴출 전적
  public int countExitHistoryByUserId(Long userId) {
    return exitHistoryRepository.countExitHistoryByUserId(userId);
  }

  public ExitHistory saveExitHistory(ExitHistory exitHistory) {
    return exitHistoryRepository.save(exitHistory);
  }

  public List<Long> getReactivatingUsers() {
    return customExitHistoryRepository.getReactivatingUsers();
  }
}
