package com.devee.devhive.domain.project.type;

public enum ProjectStatus {
    RECRUITING,           // 모집 중 (프로젝트 진행 전)
    RECRUITMENT_COMPLETE, // 모집 완료 (프로젝트 진행 중)
    RE_RECRUITMENT,       // 재 모집 (프로젝트 진행 중단)
    COMPLETE              // 프로젝트 완료
}
