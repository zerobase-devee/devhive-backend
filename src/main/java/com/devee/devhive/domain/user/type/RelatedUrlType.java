package com.devee.devhive.domain.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelatedUrlType {
    PROJECT_POST("/api/projects/%s"),       // + projectId = 프로젝트 모집글 url
    PROJECT_APPLICANTS("/api/projects/%s/applications"), // + projectId + /applications = 프로젝트 신청자 목록 조회 url
    PROJECT_INFO("/api/users/project/%s"),  // + projectId = 참여한 프로젝트 상세 url
    USER_INFO("/api/users/%s"),             // + userId = 다른 유저 정보 조회 url
    MY_INFO("/api/users/my-profile");     // 마이페이지 - 내 프로필 url

    private final String value;
}
