package com.devee.devhive.domain.user.exithistory.service;

import com.devee.devhive.domain.user.exithistory.entity.ExitHistory;
import com.devee.devhive.domain.user.exithistory.repository.ExitHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExitHistoryService {

  private final ExitHistoryRepository exitHistoryRepository;

  // 퇴출 전적
  public int countExitHistoryByUserId(Long userId) {
    return exitHistoryRepository.countExitHistoryByUserId(userId);
  }

  public ExitHistory saveExitHistory(ExitHistory exitHistory) {
    return exitHistoryRepository.save(exitHistory);
  }
}
