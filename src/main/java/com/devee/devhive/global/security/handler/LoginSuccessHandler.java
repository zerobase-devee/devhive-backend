package com.devee.devhive.global.security.handler;

import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.security.dto.TokenDto;
import com.devee.devhive.global.security.service.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenProvider tokenProvider;
  private final UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출

    String accessToken = tokenProvider.createAccessToken(email,
        authentication.getAuthorities()); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
    String refreshToken = tokenProvider.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

    tokenProvider.sendAccessAndRefreshToken(response, accessToken,
        refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답

    userRepository.findByEmail(email)
        .ifPresent(user -> {
          user.updateRefreshToken(refreshToken);
          userRepository.saveAndFlush(user);
        });
    log.info("로그인에 성공하였습니다. 이메일 : {}", email);
    log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);

    TokenDto tokenDto = TokenDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();

    String tokenJson = new ObjectMapper().writeValueAsString(tokenDto); // TokenDto 객체를 JSON 문자열로 변환

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().write(tokenJson); // JSON 문자열을 응답으로 보내기
  }

  private String extractUsername(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userDetails.getUsername();
  }
}
