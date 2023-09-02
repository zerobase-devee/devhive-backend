package com.devee.devhive.domain.user.badge.controller;

import com.devee.devhive.domain.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.user.badge.service.UserBadgeService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserBadgeController {
  private final UserBadgeService userBadgeService;

  @GetMapping("/api/users/{userId}/badges")
  public ResponseEntity<List<BadgeDto>> getUserBadges(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(userBadgeService.getUserBadges(userId).stream()
        .map(badge -> BadgeDto.from(badge.getBadge()))
        .collect(Collectors.toList())
    );
  }
}
