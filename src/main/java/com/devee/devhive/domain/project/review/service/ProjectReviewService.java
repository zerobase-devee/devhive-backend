package com.devee.devhive.domain.project.review.service;

import com.devee.devhive.domain.project.review.repository.ProjectReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectReviewService {

    private final ProjectReviewRepository projectReviewRepository;

    public double getAverageTotalScoreByTargetUserAndProject(Long targetUserId, Long projectId) {
        return projectReviewRepository.getAverageTotalScoreByTargetUserIdAndProjectId(targetUserId, projectId);
    }

}
