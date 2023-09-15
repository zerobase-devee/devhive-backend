package com.devee.devhive.global.oauth2.handler;

import static com.devee.devhive.global.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.devee.devhive.domain.auth.dto.LoginUserDto;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.config.AppProperties;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.devee.devhive.global.oauth2.util.CookieUtils;
import com.devee.devhive.global.security.dto.TokenDto;
import com.devee.devhive.global.security.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

  private final TokenService tokenService;
  private final HttpCookieOAuth2AuthorizationRequestRepository auth2AuthorizationRequestRepository;
  private final AppProperties appProperties;
  private final UserRepository userRepository;

  //oauth2인증이 성공적으로 이뤄졌을 때 실행
  //token을 포함한 uri을 생성 후 인증요청 쿠키를 비워주고 redirect
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication) throws IOException {
    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
    log.info("OAuth2 Login 성공");

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

      log.info("OAuth2로그인에 성공하였습니다. 이메일: {}", email);
    }
  }

  private String extractUsername(Authentication authentication) {
    PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
    return userDetails.getUsername();
  }

  protected String determineTargetUrl(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication) {
    Optional<String> redirectUri = CookieUtils.getCookie(request,
            REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(
        redirectUri.get())) {
      throw new IllegalArgumentException(
          "인증실패! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
    String token = tokenService.createAccessToken(authentication.getName());
    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("token", token)
        .build().toUriString();
  }

  //인증정보 요청 내역을 쿠키에서 삭제
  protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    auth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  //application.properties에 등록해놓은 Redirect uri가 맞는지 확인 (app.redirect-uris)
  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);
    log.info("uri =" + uri);
    return appProperties.getOauth2().getAuthorizedRedirectUris()
        .stream()
        .anyMatch(authorizedRedirectUri -> {
          // Only validate host and port. Let the clients use different paths if they want to
          URI authorizedURI = URI.create(authorizedRedirectUri);
          return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
              && authorizedURI.getPort() == clientRedirectUri.getPort();
        });
  }
}
