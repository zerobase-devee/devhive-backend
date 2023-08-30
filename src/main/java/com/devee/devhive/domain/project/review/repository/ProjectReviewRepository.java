package com.devee.devhive.domain.project.review.repository;

import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.repository.custom.CustomProjectReviewRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectReviewRepository extends JpaRepository<ProjectReview, Long>, CustomProjectReviewRepository {

  int countAllByProjectIdAndTargetUserId(Long projectId, Long targetUserId);

  boolean existsByProjectIdAndReviewerUserIdAndTargetUserId(Long projectId, Long reviewerUserId, Long targetUserId);
}
