package com.devee.devhive.domain.user.exithistory.controller;

import com.devee.devhive.domain.user.exithistory.service.ExitHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExitHistoryController {

  private final ExitHistoryService exitHistoryService;

  @GetMapping("/api/users/{userId}/exit-num")
  public ResponseEntity<Integer> getUserExitNum(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(exitHistoryService.countExitHistoryByUserId(userId));
  }
}
