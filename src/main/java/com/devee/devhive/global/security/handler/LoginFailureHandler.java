package com.devee.devhive.global.security.handler;

import static com.devee.devhive.global.exception.ErrorCode.NONE_CORRECT_EMAIL_AND_PW;

import com.devee.devhive.global.exception.InactivityException;
import com.devee.devhive.global.exception.OAuthProviderMissMatchException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    if (exception instanceof InactivityException) {
      // 비활성 상태 예외 처리
      response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // 405 Method Not Allowed
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/plain;charset=UTF-8");
      response.getWriter().write("퇴출전적으로 인해 계정이 비활성화되어있습니다.");
      log.info("계정 비활성화로 로그인에 실패했습니다. 메시지: {}", exception.getMessage());
    } else if(exception instanceof OAuthProviderMissMatchException) {
      // 소셜타입 불일치
      response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 ProviderType Conflict
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/plain;charset=UTF-8");
      response.getWriter().write(exception.getMessage());
      log.info("소셜타입 불일치 로그인에 실패했습니다. 메시지: {}", exception.getMessage());
    } else {
      response.setStatus(NONE_CORRECT_EMAIL_AND_PW.getHttpStatus().value());
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/plain;charset=UTF-8");
      response.getWriter().write("로그인 실패! 이메일이나 비밀번호를 확인해주세요.");
      log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
    }
  }
}
