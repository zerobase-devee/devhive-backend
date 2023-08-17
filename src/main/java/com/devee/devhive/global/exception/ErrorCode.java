package com.devee.devhive.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Auth
    INCORRECT_VERIFY_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다. "),
    EXPIRED_VERIFY_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다. "),
    FAILED_SENDING_VERIFY_CODE(HttpStatus.BAD_REQUEST, "인증 코드 전송에 실패했습니다. "),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일 입니다. "),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 가입된 닉네임 입니다. "),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다. "),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다. "),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지않은 인증입니다. "),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "인증이 만료되었습니다. "),
    NONE_CORRECT_PW(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다. "),
    NONE_CORRECT_EMAIL_AND_PW(HttpStatus.NOT_FOUND, "이메일 혹은 비밀번호를 확인하세요."),
    ;
    private final HttpStatus httpStatus;
    private final String detail;
}
