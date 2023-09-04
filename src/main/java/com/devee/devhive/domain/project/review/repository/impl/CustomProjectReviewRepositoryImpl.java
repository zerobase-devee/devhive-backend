package com.devee.devhive.domain.project.review.repository.impl;

import com.devee.devhive.domain.project.review.entity.QProjectReview;
import com.devee.devhive.domain.project.review.repository.custom.CustomProjectReviewRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomProjectReviewRepositoryImpl implements CustomProjectReviewRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public double getAverageTotalScoreByTargetUserIdAndProjectId(Long targetUserId, Long projectId) {
        QProjectReview qProjectReview = QProjectReview.projectReview;

        Double averageTotalScore = queryFactory
            .select(qProjectReview.totalScore.avg())
            .from(qProjectReview)
            .where(qProjectReview.targetUser.id.eq(targetUserId)
                .and(qProjectReview.project.id.eq(projectId)))
            .fetchOne();

        return averageTotalScore != null ? Math.round(averageTotalScore * 10.0) / 10.0 : 0.0;
    }
}
