package com.devee.devhive.domain.project.member.repository.custom;

import com.devee.devhive.domain.user.entity.dto.ProjectHistoryDto;
import java.util.List;

public interface CustomProjectMemberRepository {
    // 벌집레벨 = 유저가 참여한 프로젝트 중 완료 상태인 프로젝트의 갯수 를 조회하는 메서드
    int countCompletedProjectsByUserId(Long userId);

    // 회원 정보의 프로젝트 참여 이력을 위한 쿼리(프로젝트명, 프로젝트에서 받은 리뷰 평균점수)
    List<ProjectHistoryDto> getProjectNamesAndAverageReviewScoreByUserId(Long userId);
}
