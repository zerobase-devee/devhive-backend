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

    ALREADY_CHANGED_NICKNAME(HttpStatus.BAD_REQUEST, "최초 1회만 변경 가능하며, 이미 닉네임을 변경한 적이 있어 변경 불가합니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다."),
    USER_PASSWORD_EQUALS_NEW_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 새 비밀번호가 일치합니다."),
    NEW_PASSWORD_MISMATCH_RE_PASSWORD(HttpStatus.BAD_REQUEST, "새 비밀번호 확인이 일치하지 않습니다."),
    NOT_FOUND_PROJECT(HttpStatus.BAD_REQUEST, "프로젝트 모집 게시글을 찾을 수 없습니다. "),

    // S3
    S3_NOT_FOUND_IMAGE(HttpStatus.BAD_REQUEST, "파일을 찾을 수 없습니다. "),
    S3_NOT_SUPPORT_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일형식입니다. "),
    S3_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "파일 업로드 중 오류가 발생하였습니다. "),
    S3_DELETE_ERROR(HttpStatus.BAD_REQUEST, "파일 삭제 중 오류가 발생하였습니다. ");
    private final HttpStatus httpStatus;
    private final String detail;
}
