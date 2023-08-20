package com.devee.devhive.domain.project.review.service;

import com.devee.devhive.domain.project.review.repository.ProjectReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectReviewService {

    private final ProjectReviewRepository projectReviewRepository;

    // 프로젝트에서 유저가 받은 리뷰의 평균점수
    public double getAverageTotalScoreByTargetUserAndProject(Long targetUserId, Long projectId) {
        return projectReviewRepository.getAverageTotalScoreByTargetUserIdAndProjectId(targetUserId, projectId);
    }

}
