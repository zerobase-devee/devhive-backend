package com.devee.devhive.domain.user.exithistory.service;

import com.devee.devhive.domain.user.exithistory.repository.ExitHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExitHistoryService {

    private final ExitHistoryRepository exitHistoryRepository;

    public int countExitHistoryByUserId(Long userId) {
        return exitHistoryRepository.countExitHistoryByUserId(userId);
    }
}