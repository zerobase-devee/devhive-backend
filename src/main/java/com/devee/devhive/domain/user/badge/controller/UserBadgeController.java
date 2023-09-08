package com.devee.devhive.domain.user.badge.controller;

import com.devee.devhive.domain.user.badge.entity.dto.UserBadgeDto;
import com.devee.devhive.domain.user.badge.service.UserBadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "USER BADGE API", description = "사용자 뱃지 API")
public class UserBadgeController {
  private final UserBadgeService userBadgeService;

  @GetMapping("/api/users/{userId}/badges")
  @Operation(summary = "유저 뱃지 목록 조회")
  public ResponseEntity<List<UserBadgeDto>> getUserBadges(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(userBadgeService.getUserBadges(userId).stream()
        .map(UserBadgeDto::from)
        .collect(Collectors.toList())
    );
  }
}
