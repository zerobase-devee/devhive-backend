package com.devee.devhive.global.oauth2.handler;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.devee.devhive.global.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.type.ProviderType;
import com.devee.devhive.global.config.AppProperties;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.oauth2.domain.CustomOAuth2User;
import com.devee.devhive.global.oauth2.info.OAuth2UserInfo;
import com.devee.devhive.global.oauth2.info.OAuth2UserInfoFactory;
import com.devee.devhive.global.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.devee.devhive.global.oauth2.util.CookieUtils;
import com.devee.devhive.global.security.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenService tokenService;
  private final UserRepository userRepository;
  private final HttpCookieOAuth2AuthorizationRequestRepository auth2AuthorizationRequestRepository;
  private final AppProperties appProperties;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication)
      throws IOException {
    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to "
          + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
    log.info("OAuth2 Login 성공");
    try {
      CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

      // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
      String accessToken = tokenService.createAccessToken(oAuth2User.getEmail());
      response.addHeader(tokenService.getAccessHeader(), "Bearer " + accessToken);
      String refreshToken = tokenService.createRefreshToken();
      userRepository.findByEmail(oAuth2User.getEmail())
          .ifPresent(user -> {
            user.updateRefreshToken(refreshToken);
            userRepository.saveAndFlush(user);
          });
      loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
    } catch (CustomException e) {
      throw new CustomException(NOT_FOUND_USER);
    }
  }

  protected String determineTargetUrl(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication) {
    Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new IllegalArgumentException(
          "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
    ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

    OidcUser oidcUser = ((OidcUser) authentication.getPrincipal());
    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oidcUser.getAttributes());

    String accessToken = tokenService.createAccessToken(userInfo.getEmail());

    // refresh 토큰 설정
    String refreshToken = tokenService.createRefreshToken();
    // DB 저장
    User user = userRepository.findByEmail(userInfo.getEmail())
        .orElse(null);
    if (user != null) {
      user.setRefreshToken(refreshToken);
    }
    userRepository.saveAndFlush(Objects.requireNonNull(user));

    int cookieMaxAge = (int) (appProperties.getAuth().getRefreshTokenExpiry() / 60);

    CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
    CookieUtils.addCookie(response, REFRESH_TOKEN, refreshToken, cookieMaxAge);

    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("token", accessToken)
        .build().toUriString();
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    auth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) {
    String accessToken = tokenService.createAccessToken(oAuth2User.getEmail());
    String refreshToken = tokenService.createRefreshToken();
    response.addHeader(tokenService.getAccessHeader(), "Bearer " + accessToken);
    response.addHeader(tokenService.getRefreshHeader(), "Bearer " + refreshToken);

    tokenService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
    tokenService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);

    return appProperties.getOauth2().getAuthorizedRedirectUris()
        .stream()
        .anyMatch(authorizedRedirectUri -> {
          // Only validate host and port. Let the clients use different paths if they want to
          URI authorizedURI = URI.create(authorizedRedirectUri);
          return authorizedURI.getHost()
              .equalsIgnoreCase(clientRedirectUri.getHost())
              && authorizedURI.getPort() == clientRedirectUri.getPort();
        });
  }
}
