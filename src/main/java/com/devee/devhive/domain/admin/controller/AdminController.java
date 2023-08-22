package com.devee.devhive.domain.admin.controller;

import static com.devee.devhive.domain.user.type.Role.ADMIN;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.techstack.entity.dto.CreateTechStackDto;
import com.devee.devhive.domain.techstack.service.TechStackService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.security.service.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

  private final TechStackService techStackService;

  @PostMapping("/tech-stack")
  public void createTechStack(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestPart(value = "techStackDto") CreateTechStackDto techStackDto,
      @RequestPart(value = "image") MultipartFile imageFile) {

    User user = principal.getUser();
    if (user.getRole() != ADMIN) {
      throw new CustomException(UNAUTHORIZED);
    }
    techStackService.createTechStack(techStackDto, imageFile);
  }
}
