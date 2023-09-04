package com.devee.devhive.domain.user.controller;

import static com.devee.devhive.global.exception.ErrorCode.AVAILABLE_LOCAL_LOGIN;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.dto.MyInfoDto;
import com.devee.devhive.domain.user.entity.dto.RankUserDto;
import com.devee.devhive.domain.user.entity.dto.UserInfoDto;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.favorite.service.FavoriteService;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.domain.user.type.ProviderType;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 마이페이지 - 내 프로필, 비밀번호 변경 랭킹 목록 페이지 다른 유저 프로필 페이지
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final FavoriteService favoriteService;

  /**
   * 다른 유저 기본 정보 조회
   * UserInfoDto - userId, nickname, profileImage url, intro, isFavorite : 로그인한 유저가 조회할 경우 상대가 관심유저인지 여부
   */
  @GetMapping("/{userId}")
  public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable("userId") Long targetUserId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User targetUser = userService.getUserById(targetUserId);
    boolean isFavorite = false;

    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      // 로그인한 상태일 때 동작
      PrincipalDetails details = (PrincipalDetails) authentication.getPrincipal();
      User loggedInUser = userService.getUserByEmail(details.getEmail());
      isFavorite = favoriteService.isFavorite(loggedInUser.getId(), targetUserId);
    }

    return ResponseEntity.ok(UserInfoDto.of(targetUser, isFavorite));
  }

  /**
   * 내 기본 정보 조회
   * MyInfoDto - userId, email, region, nickname, isLocalLogin : 일반로그인유저 여부, profileImage url, intro
   */
  @GetMapping("/my-profile")
  public ResponseEntity<MyInfoDto> getMyInfo(@AuthenticationPrincipal PrincipalDetails principal) {
    User user = userService.getUserByEmail(principal.getEmail());
    return ResponseEntity.ok(MyInfoDto.of(user));
  }

  // 내 기본 정보 수정
  @PutMapping("/my-profile")
  public void updateBasicInfo(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody @Valid UpdateBasicInfoForm form
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    userService.updateBasicInfo(user, form);
  }

  // 비밀번호 변경
  @PutMapping("/password")
  public void updatePassword(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody @Valid UpdatePasswordForm form
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (user.getProviderType() != ProviderType.LOCAL) {
      throw new CustomException(AVAILABLE_LOCAL_LOGIN);
    }
    userService.updatePassword(user, form);
  }

  // 내 프로필 사진 수정
  @PutMapping("/my-profile/image")
  public void updateProfileImage(
      @RequestPart(value = "image", required = false) MultipartFile multipartFile,
      @AuthenticationPrincipal PrincipalDetails principal
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    userService.updateProfileImage(multipartFile, user);
  }

  // 내 프로필 사진 삭제
  @DeleteMapping("/my-profile/image")
  public void deleteProfileImage(@AuthenticationPrincipal PrincipalDetails principal) {
    User user = userService.getUserByEmail(principal.getEmail());
    userService.deleteProfileImage(user);
  }

  // 랭킹 목록 페이징 처리
  @GetMapping("/rank")
  public ResponseEntity<Page<RankUserDto>> getRankUsers(Pageable pageable) {
    return ResponseEntity.ok(userService.getRankUsers(pageable).map(RankUserDto::from));
  }
}
