package com.devee.devhive.domain.project.review.repository;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectReviewRepository extends JpaRepository<ProjectReview, Long> {
    // 유저가 특정 프로젝트에서 받은 리뷰 총점을 가져옴
    @Query("SELECT SUM(pr.totalScore) " +
        "FROM ProjectReview pr " +
        "WHERE pr.targetUser = :user AND pr.project = :project")
    Integer getTotalScoreByUserAndProject(@Param("user") User user, @Param("project") Project project);
}
