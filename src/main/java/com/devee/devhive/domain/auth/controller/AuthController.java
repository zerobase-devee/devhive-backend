package com.devee.devhive.domain.auth.controller;

import com.devee.devhive.domain.auth.dto.EmailDTO;
import com.devee.devhive.domain.auth.dto.JoinDTO;
import com.devee.devhive.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

  private final AuthService authService;

  // 인증 코드
  @PostMapping("/verify")
  public void sendVerificationCode(@RequestBody EmailDTO emailDTO) throws Exception {

    authService.getVerificationCode(emailDTO);
  }

  // 유저 회원가입
  @PostMapping("/signup")
  public void signUp(@RequestBody JoinDTO joinDTO) {

    authService.signUp(joinDTO);
  }
}
