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
    PLEASE_CHANGE_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임 변경 후 이용바랍니다. "),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다. "),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다. "),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지않은 인증입니다. "),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "인증이 만료되었습니다. "),
    NONE_CORRECT_PW(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다. "),
    NONE_CORRECT_EMAIL_AND_PW(HttpStatus.NOT_FOUND, "이메일 혹은 비밀번호를 확인하세요."),

    // OAuth2
    CHECK_SOCIAL_SERVER(HttpStatus.NOT_FOUND, "소셜로그인에 실패하였습니다. 서버를 확인 하세요."),

    ALREADY_CHANGED_NICKNAME(HttpStatus.BAD_REQUEST, "최초 1회만 변경 가능하며, 이미 닉네임을 변경한 적이 있어 변경 불가합니다."),
    AVAILABLE_LOCAL_LOGIN(HttpStatus.BAD_REQUEST, "소셜 로그인 유저는 변경 불가."),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다."),
    USER_PASSWORD_EQUALS_NEW_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 새 비밀번호가 일치합니다."),
    NEW_PASSWORD_MISMATCH_RE_PASSWORD(HttpStatus.BAD_REQUEST, "새 비밀번호 확인이 일치하지 않습니다."),
    PROJECT_CANNOT_DELETED(HttpStatus.BAD_REQUEST, "프로젝트 상태가 진행중이거나 완료상태인 경우 삭제할 수 없습니다. "),

    // ProjectApply
    NOT_FOUND_APPLICATION(HttpStatus.BAD_REQUEST, "해당 신청건을 찾을 수 없습니다."),
    RECRUITMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 프로젝트 모집이 완료되었습니다."),
    PROJECT_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "이미 프로젝트를 신청한 상태입니다."),
    APPLICATION_ALREADY_ACCEPT(HttpStatus.BAD_REQUEST, "이미 신청하여 승인된 상태입니다."),
    APPLICATION_ALREADY_REJECT(HttpStatus.BAD_REQUEST, "이미 신청하여 거절된 상태입니다."),
    APPLICATION_STATUS_NOT_PENDING(HttpStatus.BAD_REQUEST, "신청이 승인/거절 되어 신청대기 상태가 아닙니다."),

    // comment, reply
    NOT_FOUND_COMMENT(HttpStatus.BAD_REQUEST, "해당 댓글을 찾을 수 없습니다."),
    NOT_FOUND_REPLY(HttpStatus.BAD_REQUEST, "해당 대댓글을 찾을 수 없습니다."),

    NOT_FOUND_ALARM(HttpStatus.BAD_REQUEST, "해당 알림을 찾을 수 없습니다."),

    NOT_FOUND_CAREER(HttpStatus.BAD_REQUEST, "해당 경력을 찾을 수 없습니다."),
    NOT_FOUND_BOOKMARK(HttpStatus.BAD_REQUEST, "해당 북마크를 찾을 수 없습니다."),
    NOT_FOUND_FAVORITE(HttpStatus.BAD_REQUEST, "해당 관심유저를 찾을 수 없습니다."),

    // S3
    S3_NOT_FOUND_IMAGE(HttpStatus.BAD_REQUEST, "파일을 찾을 수 없습니다."),
    S3_FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일용량은 5MB 이하만 업로드 가능합니다."),
    S3_NOT_SUPPORT_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일형식입니다."),
    S3_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "파일 업로드 중 오류가 발생하였습니다."),
    S3_DELETE_ERROR(HttpStatus.BAD_REQUEST, "파일 삭제 중 오류가 발생하였습니다."),

    // Project
    NOT_FOUND_PROJECT(HttpStatus.BAD_REQUEST, "프로젝트 모집 게시글을 찾을 수 없습니다. "),
    NOT_YOUR_PROJECT(HttpStatus.BAD_REQUEST, "해당 프로젝트에 참여하고 있지 않습니다."),
    PROJECT_NOT_COMPLETE(HttpStatus.BAD_REQUEST, "해당 프로젝트는 이미 완료되었습니다."),
    ALREADY_SUBMIT_TARGETUSER(HttpStatus.BAD_REQUEST, "해당 유저에 대한 리뷰가 이미 존재합니다."),

    // Vote
    NOT_FOUND_VOTE(HttpStatus.BAD_REQUEST, "해당 투표를 찾을 수 없습니다."),
    NOT_YOUR_VOTE(HttpStatus.BAD_REQUEST, "유저가 가진 투표권이 아닙니다."),
    ALREADY_REGISTERED_VOTE(HttpStatus.BAD_REQUEST, "진행중인 투표가 있습니다."),
    ALREADY_SUBMIT_VOTE(HttpStatus.BAD_REQUEST, "투표를 이미 진행했습니다."),

    NOT_FOUND_TECH_STACK(HttpStatus.NOT_FOUND, "테크스택을 찾을 수 없습니다. "),
    DUPLICATE_TECH_STACK(HttpStatus.BAD_REQUEST, "이미 등록된 테크스택입니다. "),
    NOT_FOUND_BADGE(HttpStatus.NOT_FOUND, "뱃지를 찾을 수 없습니다. "),
    DUPLICATE_BADGE(HttpStatus.BAD_REQUEST, "이미 등록된 뱃지입니다. "),

    // Chat
    ALREADY_CREATE_CHATROOM(HttpStatus.BAD_REQUEST, "해당 프로젝트에 대한 채팅방이 이미 존재합니다. "),
    ALREADY_ENTER_CHATROOM(HttpStatus.BAD_REQUEST, "이미 참여한 채팅방입니다."),
    NOT_FOUND_CHATROOM(HttpStatus.NOT_FOUND, "해당 채팅방을 찾을 수 없습니다."),
    NOT_FOUND_CHATMEMBER(HttpStatus.NOT_FOUND, "해당 채팅방에 참여하고 있지 않습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String detail;
}
