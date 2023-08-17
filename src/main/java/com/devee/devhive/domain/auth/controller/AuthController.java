package com.devee.devhive.domain.auth.controller;

import com.devee.devhive.domain.auth.dto.EmailDTO;
import com.devee.devhive.domain.auth.dto.JoinDTO;
import com.devee.devhive.domain.auth.service.AuthService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.security.service.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

  private final UserRepository userRepository;
  private final AuthService authService;
  private final TokenProvider tokenProvider;

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

  @PostMapping("/refresh")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public String reissueAccessToken(HttpServletResponse response, @RequestBody String refreshToken) {
    log.info("전달받은 refreshToken : {}", refreshToken);
    // AccessToken 재발급
    Optional<User> userOptional = userRepository.findByRefreshToken(refreshToken);
    log.info("userOptional : {}", userOptional);
    if (userOptional.isEmpty()) {
      return null;
    }
    User user = userOptional.get();
    log.info("user : {}", user);
    String reIssuedRefreshToken = reIssueRefreshToken(user);
    String accessToken = tokenProvider.createAccessToken(user.getEmail(), user.getAuthorities());
    log.info("accessToken : {}", accessToken);
    log.info("reIssuedRefreshToken : {}", reIssuedRefreshToken);
    tokenProvider.sendAccessToken(response, accessToken);
    tokenProvider.sendAccessAndRefreshToken(response, accessToken, reIssuedRefreshToken);
    return accessToken;
  }

  private String reIssueRefreshToken(User user) {
    String reIssuedRefreshToken = tokenProvider.createRefreshToken();
    user.updateRefreshToken(reIssuedRefreshToken);
    userRepository.saveAndFlush(user);
    return reIssuedRefreshToken;
  }
}
