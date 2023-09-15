package com.devee.devhive.global.oauth2.handler;

import static com.devee.devhive.global.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.devee.devhive.global.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.devee.devhive.global.oauth2.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final HttpCookieOAuth2AuthorizationRequestRepository auth2AuthorizationRequestRepository;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    log.info("소셜로그인 인증실패");
    if (exception instanceof OAuth2AuthenticationException) {
      // 비활성 상태 예외 처리
      response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // 405 Method Not Allowed
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/plain;charset=UTF-8");
      response.getWriter().write(exception.getMessage());
      log.info("계정 비활성화로 로그인에 실패했습니다. 메시지: {}", exception.getMessage());
    } else if(exception instanceof InternalAuthenticationServiceException) {
      // 소셜타입 불일치
      response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 ProviderType Conflict
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/plain;charset=UTF-8");
      response.getWriter().write(exception.getMessage());
      log.info("소셜타입 불일치 로그인에 실패했습니다. 메시지: {}", exception.getMessage());
    }else {
    super.onAuthenticationFailure(request, response, exception);
      String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
          .map(Cookie::getValue)
          .orElse(("/"));

      targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
          .queryParam("error", exception.getLocalizedMessage())
          .build().toUriString();

      auth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

      getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
  }
}
