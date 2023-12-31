package com.devee.devhive.global.security.filter;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 로그인 이외의 요청이 오면 처리하는 필터 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는
 * 않는다. 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR 3. RefreshToken이 있는
 * 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급(RTR 방식) 인증 성공 처리는 하지 않고 실패
 * 처리
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

  private static final String NO_CHECK_URL = "/api/auth/signin"; // "/api/auth/signin"으로 들어오는 요청은 Filter 작동 X
  private static final String NO_CHECK_URL2 = "/login"; // "/api/auth/signin"으로 들어오는 요청은 Filter 작동 X
  private final TokenService tokenService;
  private final UserRepository userRepository;

  private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (request.getRequestURI().equals(NO_CHECK_URL) || request.getRequestURI().equals(NO_CHECK_URL2)) {
      filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
      return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
    }

    // 사용자 요청 헤더에서 RefreshToken 추출
    // -> RefreshToken이 없거나 유효하지 않다면(DB에 저장된 RefreshToken과 다르다면) null을 반환
    // 사용자의 요청 헤더에 RefreshToken이 있는 경우는, AccessToken이 만료되어 요청한 경우밖에 없다.
    // 따라서, 위의 경우를 제외하면 추출한 refreshToken은 모두 null
    String refreshToken = tokenService.extractRefreshToken(request)
        .filter(tokenService::isTokenValid)
        .orElse(null);

    // 리프레시 토큰이 요청 헤더에 존재했다면, 사용자가 AccessToken이 만료되어서
    // RefreshToken까지 보낸 것이므로 리프레시 토큰이 DB의 리프레시 토큰과 일치하는지 판단 후,
    // 일치한다면 AccessToken을 재발급해준다.
    if (refreshToken != null) {
      checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
      return; // RefreshToken을 보낸 경우에는 AccessToken을 재발급 하고 인증 처리는 하지 않게 하기위해 바로 return으로 필터 진행 막기
    }

    // RefreshToken이 없거나 유효하지 않다면, AccessToken을 검사하고 인증을 처리하는 로직 수행
    // AccessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
    // AccessToken이 유효하다면, 인증 객체가 담긴 상태로 다음 필터로 넘어가기 때문에 인증 성공
    checkAccessTokenAndAuthentication(request, response, filterChain);
  }

  /**
   * [리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드] 파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 DB에서 유저를 찾고, 해당 유저가
   * 있다면 TokenService.createAccessToken()으로 AccessToken 생성, reIssueRefreshToken()로 리프레시 토큰 재발급 &
   * DB에 리프레시 토큰 업데이트 메소드 호출 그 후 TokenService.sendAccessTokenAndRefreshToken()으로 응답 헤더에 보내기
   */
  public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
    userRepository.findByRefreshToken(refreshToken)
        .ifPresent(user -> {
          String reIssuedRefreshToken = reIssueRefreshToken(user);
          tokenService.sendAccessAndRefreshToken(response,
              tokenService.createAccessToken(user.getEmail()),
              reIssuedRefreshToken);
        });
  }

  /**
   * [리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드] TokenService.createRefreshToken()으로 리프레시 토큰 재발급 후 DB에
   * 재발급한 리프레시 토큰 업데이트 후 Flush
   */
  private String reIssueRefreshToken(User user) {
    String reIssuedRefreshToken = tokenService.createRefreshToken();
    user.updateRefreshToken(reIssuedRefreshToken);
    userRepository.saveAndFlush(user);
    return reIssuedRefreshToken;
  }

  /**
   * [액세스 토큰 체크 & 인증 처리 메소드] request에서 extractAccessToken()으로 액세스 토큰 추출 후, isTokenValid()로 유효한 토큰인지
   * 검증 유효한 토큰이면, 액세스 토큰에서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환 그 유저 객체를
   * saveAuthentication()으로 인증 처리하여 인증 허가 처리된 객체를 SecurityContextHolder에 담기 그 후 다음 인증 필터로 진행
   */
  public void checkAccessTokenAndAuthentication(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = tokenService.extractAccessToken(request);
    if (token != null && tokenService.isTokenValid(token)) {
      Optional<String> email = tokenService.extractEmail(token);
      email.flatMap(userRepository::findByEmail)
          .ifPresent(this::saveAuthentication);
    }

    filterChain.doFilter(request, response);
  }

  public void saveAuthentication(User myUser) {

    PrincipalDetails principalDetails = PrincipalDetails.create(myUser);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            principalDetails, null,
            authoritiesMapper.mapAuthorities(principalDetails.getAuthorities())
        );

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}