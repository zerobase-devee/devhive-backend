package com.devee.devhive.domain.user.exithistory.controller;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.exithistory.entity.ExitHistory;
import com.devee.devhive.domain.user.exithistory.service.ExitHistoryService;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.domain.user.type.ActivityStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "EXIT HISTORY API", description = "퇴출 전적 API")
public class ExitHistoryController {

  private final ExitHistoryService exitHistoryService;
  private final UserService userService;

  @Operation(summary = "유저 퇴출 횟수 조회")
  @GetMapping("/api/users/{userId}/exit-num")
  public ResponseEntity<Integer> getUserExitNum(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(exitHistoryService.countExitHistoryByUserId(userId));
  }

  @Operation(summary = "유저 퇴출 처리", description = "퇴출 횟수를 기반으로 유저 비활성화, 퇴출 처리")
  @PostMapping("/api/users/{userId}/exit-process")
  public void userExit(@PathVariable("userId") Long userId) {
    User user = userService.getUserById(userId);
    // 타겟 유저 퇴출전적
    int exitedCount = exitHistoryService.countExitHistoryByUserId(userId);
    // 퇴출 횟수 당 1주로 유저 비활성화 기간 설정(이번이 10회째인 경우 영구 비활성화)
    LocalDateTime reActiveDate = exitedCount < 9 ?
        LocalDateTime.now().plus(exitedCount + 1, ChronoUnit.WEEKS) : LocalDateTime.MAX;

    exitHistoryService.saveExitHistory(ExitHistory.builder()
        .user(user)
        .reActiveDate(reActiveDate)
        .build());
    userService.setUserStatus(user, ActivityStatus.INACTIVITY);
  }
}
