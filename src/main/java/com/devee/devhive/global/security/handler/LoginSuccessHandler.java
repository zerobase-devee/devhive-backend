package com.devee.devhive.global.security.handler;

import com.devee.devhive.domain.auth.dto.LoginUserDto;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.security.dto.TokenDto;
import com.devee.devhive.global.security.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenService tokenService;
  private final UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출

    User user = userRepository.findByEmail(email).orElse(null);

    if (user != null) {
      // AccessToken 및 RefreshToken 생성
      String accessToken = tokenService.createAccessToken(email);
      String refreshToken = tokenService.createRefreshToken();

      // 응답 헤더에 AccessToken 및 RefreshToken 추가
      tokenService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

      // 사용자의 RefreshToken 업데이트
      user.updateRefreshToken(refreshToken);
      userRepository.saveAndFlush(user);

      log.info("로그인에 성공하였습니다. 이메일: {}", email);
      log.info("로그인에 성공하였습니다. AccessToken: {}", accessToken);

      // TokenDto 생성
      TokenDto tokenDto = TokenDto.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .userDto(LoginUserDto.from(user))
          .build();

      // TokenDto를 JSON 문자열로 변환하여 응답
      String tokenJson = new ObjectMapper().writeValueAsString(tokenDto);

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write(tokenJson);
    }
  }

  private String extractUsername(Authentication authentication) {
    PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
    return userDetails.getUsername();
  }
}
