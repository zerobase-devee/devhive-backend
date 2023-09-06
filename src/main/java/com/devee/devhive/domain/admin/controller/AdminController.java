package com.devee.devhive.domain.admin.controller;

import static com.devee.devhive.domain.user.type.Role.ADMIN;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.badge.entity.dto.CreateBadgeDto;
import com.devee.devhive.domain.badge.service.BadgeService;
import com.devee.devhive.domain.techstack.entity.dto.CreateTechStackDto;
import com.devee.devhive.domain.techstack.service.TechStackService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "ADMIN API", description = "관리자 API")
public class AdminController {

  private final UserService userService;
  private final TechStackService techStackService;
  private final BadgeService badgeService;
  private final S3Service s3Service;

  @PostMapping("/image")
  @Operation(summary = "이미지 업로드", description = "이미지의 URL을 반환")
  public String uploadImage(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestPart(value = "image", required = false) MultipartFile multipartFile
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (user.getRole() != ADMIN) {
      throw new CustomException(UNAUTHORIZED);
    }

    return s3Service.upload(multipartFile);
  }

  @PostMapping("/tech-stack")
  @Operation(summary = "테크스택 생성", description = "이름과 이미지 URL을 사용하여 테크스택 생성")
  public void createTechStack(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody CreateTechStackDto createTechStackDto
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (user.getRole() != ADMIN) {
      throw new CustomException(UNAUTHORIZED);
    }

    techStackService.createTechStack(createTechStackDto);
  }

  @DeleteMapping("/tech-stack/{techStackId}")
  @Operation(summary = "테크스택 삭제", description = "테크스택의 고유 ID로 테크스택 삭제")

  public void deleteTechStack(
      @AuthenticationPrincipal PrincipalDetails principal, @PathVariable Long techStackId
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (user.getRole() != ADMIN) {
      throw new CustomException(UNAUTHORIZED);
    }

    techStackService.deleteTechStack(techStackId);
  }

  @PostMapping("/badge")
  @Operation(summary = "뱃지 생성", description = "이름과 이미지 URL을 사용하여 뱃지 생성")
  public void createBadge(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody CreateBadgeDto createBadgeDto
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (user.getRole() != ADMIN) {
      throw new CustomException(UNAUTHORIZED);
    }

    badgeService.createBadge(createBadgeDto);
  }

  @DeleteMapping("/badge/{badgeId}")
  @Operation(summary = "뱃지 삭제", description = "뱃지의 고유 ID로 뱃지 삭제")
  public void deleteBadge(
      @AuthenticationPrincipal PrincipalDetails principal, @PathVariable Long badgeId
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (user.getRole() != ADMIN) {
      throw new CustomException(UNAUTHORIZED);
    }
    badgeService.deleteBadge(badgeId);
  }
}
