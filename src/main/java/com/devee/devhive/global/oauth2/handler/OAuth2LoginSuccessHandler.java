package com.devee.devhive.global.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements
    AuthenticationSuccessHandler {

  // private final UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    log.info("onAuthenticationSuccess() 실행");

    try {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String email = userDetails.getUsername();

      log.info(email + "로그인 완료.");
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
