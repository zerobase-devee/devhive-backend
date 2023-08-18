package com.devee.devhive.global.security.filter;

import com.devee.devhive.domain.auth.dto.LoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//로그인 처리 필터
public class CustomJsonUsernamePasswordAuthenticationFilter extends
    AbstractAuthenticationProcessingFilter {

  private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/auth/signin"; // "/v1/auth/signin"으로 오는 요청을 처리
  private static final String HTTP_METHOD = "POST"; // 로그인 HTTP 메소드는 POST
  private static final String CONTENT_TYPE = "application/json"; // JSON 타입의 데이터로 오는 로그인 요청만 처리
  private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
      new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL,
          HTTP_METHOD);
  // "/api/auth/signin" + POST로 온 요청에 매칭

  private final ObjectMapper objectMapper;

  public CustomJsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
    super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER); // 위에서 설정한 "login" + POST로 온 요청을 처리하기 위해 설정
    this.objectMapper = objectMapper;
  }

  //인증처리
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException {
    if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
      throw new AuthenticationServiceException(
          "Authentication Content-Type not supported: " + request.getContentType());
    }
//    String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//
//    var usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
//
//    Object email = usernamePasswordMap.get(USERNAME_KEY);
//    Object password = usernamePasswordMap.get(PASSWORD_KEY);
//
//    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email,
//        password);//principal 과 credentials 전달
//
//    return this.getAuthenticationManager().authenticate(authRequest);

    // LoginDto 객체로 매핑
    ObjectMapper om = new ObjectMapper();
    LoginDto loginDto = om.readValue(request.getInputStream(), LoginDto.class);
    // 인증 정보를 사용하여 UsernamePasswordAuthenticationToken 객체 생성
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDto.getEmail(),
        loginDto.getPassword());

    // 인증 처리 시작 -> 결과 반환
    return getAuthenticationManager().authenticate(authenticationToken);
  }
}
