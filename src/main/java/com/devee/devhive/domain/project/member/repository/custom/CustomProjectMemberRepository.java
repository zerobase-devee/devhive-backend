package com.devee.devhive.domain.project.member.repository.custom;

public interface CustomProjectMemberRepository {
    // 벌집레벨 = 유저가 참여한 프로젝트 중 완료 상태인 프로젝트의 갯수 를 조회하는 메서드
    int countCompletedProjectsByUserId(Long userId);
}
