package com.devee.devhive.domain.user.controller;

import com.devee.devhive.domain.user.entity.dto.RankUserDto;
import com.devee.devhive.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RankController {

  private final UserService userService;

  // 랭킹 목록 조회
  @GetMapping("/api/rank/users")
  @Operation(summary = "랭킹 목록 조회")
  public ResponseEntity<List<RankUserDto>> getRankUsers(Pageable pageable) {
    return ResponseEntity.ok(userService.getRankUsers(pageable));
  }
}
