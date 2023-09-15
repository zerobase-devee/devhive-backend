package com.devee.devhive.domain.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmContent {
    COMMENT("에 새로운 댓글이 달렸습니다."),
    REPLY("에 작성한 내 댓글에 답글이 달렸습니다."),
    PROJECT_APPLY("에 새로운 신청자가 있습니다."),
    APPLICANT_ACCEPT("에 신청이 수락 되었습니다."),
    APPLICANT_REJECT("에 신청이 거절 되었습니다."),
    EXIT_VOTE("님에 대한 프로젝트 퇴출 투표가 생성되었습니다. 24시간내에 투표 참여 바랍니다."),
    VOTE_RESULT_EXIT_SUCCESS("님이 프로젝트에서 퇴출 되었습니다."),
    VOTE_RESULT_EXIT_FAIL("님의 퇴출 투표결과가 무효되었습니다."),
    DELETE_PROJECT("프로젝트가 삭제되었습니다."),
    REVIEW_REQUEST("은/는 어떠셨나요? 팀원 평가를 진행해주세요."),
    REVIEW_RESULT("의 팀원 평가가 완료되었습니다."),
    FAVORITE_USER("님이 새로운 프로젝트를 업로드 하였습니다."),
    RECOMMEND("님에게 새로운 추천 프로젝트가 업데이트 되었습니다.");

    private final String value;
}
