package com.devee.devhive.domain.admin.controller;

import static com.devee.devhive.domain.user.type.Role.ADMIN;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.techstack.entity.dto.CreateTechStackDto;
import com.devee.devhive.domain.techstack.service.TechStackService;
import com.devee.devhive.domain.user.badge.entity.dto.CreateBadgeDto;
import com.devee.devhive.domain.user.badge.service.BadgeService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.entity.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

  private final UserService userService;
  private final TechStackService techStackService;
  private final BadgeService badgeService;

  @PostMapping("/tech-stack")
  public void createTechStack(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestPart(value = "techStackDto") CreateTechStackDto techStackDto,
      @RequestPart(value = "image") MultipartFile imageFile
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (user.getRole() != ADMIN) {
      throw new CustomException(UNAUTHORIZED);
    }
    techStackService.createTechStack(techStackDto, imageFile);
  }

  @DeleteMapping("/tech-stack/{techStackId}")
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
  public void createBadge(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestPart(value = "badgeDto") CreateBadgeDto badgeDto,
      @RequestPart(value = "image") MultipartFile imageFile
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (user.getRole() != ADMIN) {
      throw new CustomException(UNAUTHORIZED);
    }
    badgeService.createBadge(badgeDto, imageFile);
  }

  @DeleteMapping("/badge/{badgeId}")
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
