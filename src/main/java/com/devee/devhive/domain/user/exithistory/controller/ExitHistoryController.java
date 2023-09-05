package com.devee.devhive.domain.user.exithistory.controller;

import com.devee.devhive.domain.user.exithistory.service.ExitHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "EXIT HISTORY API", description = "퇴출 전적 API")
public class ExitHistoryController {

  private final ExitHistoryService exitHistoryService;

  @Operation(summary = "유저 퇴출 횟수 조회")
  @GetMapping("/api/users/{userId}/exit-num")
  public ResponseEntity<Integer> getUserExitNum(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(exitHistoryService.countExitHistoryByUserId(userId));
  }
}
