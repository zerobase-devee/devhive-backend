package com.devee.devhive.domain.auth.controller;

import com.devee.devhive.domain.auth.dto.EmailDto;
import com.devee.devhive.domain.auth.dto.JoinDto;
import com.devee.devhive.domain.auth.dto.NicknameDto;
import com.devee.devhive.domain.auth.dto.VerifyDto;
import com.devee.devhive.domain.auth.service.AuthService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.security.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
  private final TokenService tokenService;

  // 인증 코드 전송
  @PostMapping("/verify/send")
  public void sendVerificationCode(@RequestBody @Valid EmailDto emailDto) throws Exception {
    authService.sendVerificationCode(emailDto);
  }

  // 인증 코드 검증
  @PostMapping("/verify/check")
  public boolean checkVerificationCode(@RequestBody VerifyDto verifyDto) {
    return authService.checkVerificationCode(verifyDto);
  }

  // 유저 회원가입
  @PostMapping("/signup")
  public void signUp(@RequestBody @Valid JoinDto joinDto) {
    authService.signUp(joinDto);
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
    String accessToken = tokenService.createAccessToken(user.getEmail());
    log.info("accessToken : {}", accessToken);
    log.info("reIssuedRefreshToken : {}", reIssuedRefreshToken);
    tokenService.sendAccessToken(response, accessToken);
    tokenService.sendAccessAndRefreshToken(response, accessToken, reIssuedRefreshToken);
    return accessToken;
  }

  private String reIssueRefreshToken(User user) {
    String reIssuedRefreshToken = tokenService.createRefreshToken();
    user.updateRefreshToken(reIssuedRefreshToken);
    userRepository.saveAndFlush(user);
    return reIssuedRefreshToken;
  }

  @PostMapping("/check-nickname")
  public boolean checkNickname(@RequestBody NicknameDto nicknameDto) {
    return authService.isNicknameAvailable(nicknameDto);
  }
}
