package com.devee.devhive.domain.project.review.repository.custom;

public interface CustomProjectReviewRepository {
    double getAverageTotalScoreByTargetUserIdAndProjectId(Long targetUserId, Long projectId);
}
