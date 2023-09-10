package com.devee.devhive.domain.project.review.repository;

import com.devee.devhive.domain.project.review.entity.ProjectReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectReviewRepository extends JpaRepository<ProjectReview, Long> {

  boolean existsByProjectIdAndReviewerUserIdAndTargetUserId(Long projectId, Long reviewerUserId, Long targetUserId);

  List<ProjectReview> findAllByProjectId(Long projectId);
}
