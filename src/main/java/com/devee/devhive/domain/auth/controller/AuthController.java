package com.devee.devhive.domain.auth.controller;

import com.devee.devhive.domain.auth.dto.EmailDto;
import com.devee.devhive.domain.auth.dto.JoinDto;
import com.devee.devhive.domain.auth.dto.LoginUserDto;
import com.devee.devhive.domain.auth.dto.NicknameDto;
import com.devee.devhive.domain.auth.dto.VerifyDto;
import com.devee.devhive.domain.auth.service.AuthService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.security.dto.TokenDto;
import com.devee.devhive.global.security.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@Tag(name = "AUTH API", description = "검증 API")
public class AuthController {

  private final UserRepository userRepository;
  private final AuthService authService;
  private final TokenService tokenService;

  // 인증 코드 전송
  @PostMapping("/verify/send")
  @Operation(summary = "이메일 인증코드 전송", description = "사용자의 이메일로 영문 + 숫자 랜덤 6자리 전송")
  public void sendVerificationCode(@RequestBody @Valid EmailDto emailDto) throws Exception {
    authService.sendVerificationCode(emailDto);
  }

  // 인증 코드 검증
  @PostMapping("/verify/check")
  @Operation(summary = "이메일 인증코드 검증", description = "사용자가 입력한 이메일 인증코드를 검증")
  public boolean checkVerificationCode(@RequestBody VerifyDto verifyDto) {
    return authService.checkVerificationCode(verifyDto);
  }

  // 유저 회원가입
  @PostMapping("/signup")
  @Operation(summary = "회원가입")
  public void signUp(@RequestBody @Valid JoinDto joinDto) {
    authService.signUp(joinDto);
  }

  @PostMapping("/logout")
  public void logout(HttpSession session, HttpServletRequest request, HttpServletResponse response,
      @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
    // 유효기간 만료된 토큰 설정
    tokenService.expireAccessToken(response, principalDetails.getEmail());
    // 세션 무효화
    session.invalidate();

    // 쿠키 삭제
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        // 삭제하려는 쿠키의 이름을 지정하고 경로와 도메인을 설정해야 합니다.
        if ("RefreshToken".equals(cookie.getName()) || "JSESSIONID".equals(cookie.getName())) {
          Cookie deletedCookie = new Cookie(cookie.getName(), null);
          deletedCookie.setMaxAge(0); // 쿠키 만료 시간을 0으로 설정하여 삭제
          deletedCookie.setPath("/"); // 쿠키의 경로 설정 (루트 경로로 설정하면 모든 경로에서 쿠키 삭제 가능)
          // 도메인 설정 (옵션)
          // deletedCookie.setDomain("example.com"); // 쿠키의 도메인 설정
          response.addCookie(deletedCookie); // 쿠키를 응답에 추가하여 삭제
        }
      }
    }
    // 로그아웃 후 리디렉트
    response.sendRedirect("/");

    log.info("로그아웃 및 쿠키 삭제 완료");
  }

  @PostMapping("/refresh")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Operation(summary = "토큰 리프레쉬", description = "사용자의 Refresh Token 으로 Access Token 재발급")
  public ResponseEntity<TokenDto> reissueAccessToken(HttpServletResponse response, @RequestBody String refreshToken) {
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

    return ResponseEntity.ok(TokenDto.builder()
        .accessToken(accessToken)
        .refreshToken(reIssuedRefreshToken)
        .userDto(LoginUserDto.from(user))
        .build()
    );
  }

  private String reIssueRefreshToken(User user) {
    String reIssuedRefreshToken = tokenService.createRefreshToken();
    user.updateRefreshToken(reIssuedRefreshToken);
    userRepository.saveAndFlush(user);
    return reIssuedRefreshToken;
  }

  @PostMapping("/check-nickname")
  @Operation(summary = "닉네임 중복 체크")
  public boolean checkNickname(@RequestBody NicknameDto nicknameDto) {
    return authService.isNicknameAvailable(nicknameDto);
  }
}
