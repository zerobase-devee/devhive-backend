package com.devee.devhive.domain.user.favorite.controller;

import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.favorite.entity.Favorite;
import com.devee.devhive.domain.user.favorite.entity.dto.FavoriteDto;
import com.devee.devhive.domain.user.favorite.service.FavoriteService;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 - 관심유저 목록 조회 관심유저 등록/해제
 */
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
@Tag(name = "FAVORITE API", description = "관심유저 API")
public class FavoriteController {

  private final FavoriteService favoriteService;
  private final UserService userService;

  // 관심 유저 등록
  @PostMapping("/users/{userId}")
  @Operation(summary = "관심 유저 등록")
  public void register(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable("userId") Long targetUserId
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    User favoriteUser = userService.getUserById(targetUserId);
    favoriteService.register(user, favoriteUser);
  }

  // 관심 유저 삭제
  @DeleteMapping("/{favoriteId}")
  @Operation(summary = "관심 유저 삭제")
  public void delete(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable("favoriteId") Long favoriteId
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    Favorite favorite = favoriteService.findById(favoriteId);
    if (!Objects.equals(user.getId(), favorite.getUser().getId())) {
      throw new CustomException(UNAUTHORIZED);
    }
    favoriteService.delete(favorite);
  }

  // 관심 유저 목록 조회
  @GetMapping
  @Operation(summary = "관심 유저 목록 조회")
  public ResponseEntity<Page<FavoriteDto>> getFavoriteUsers(
      @AuthenticationPrincipal PrincipalDetails principal, Pageable pageable
  ) {
    User user = userService.getUserByEmail(principal.getEmail());

    return ResponseEntity.ok(favoriteService.getFavoriteUsers(user.getId(), pageable)
            .map(FavoriteDto::from)
    );
  }
}
