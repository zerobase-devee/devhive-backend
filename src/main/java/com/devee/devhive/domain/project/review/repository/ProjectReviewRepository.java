package com.devee.devhive.domain.project.review.repository;

import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.repository.custom.CustomProjectReviewRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectReviewRepository extends JpaRepository<ProjectReview, Long>, CustomProjectReviewRepository {

  boolean existsByProjectIdAndTargetUserId(Long projectId, Long targetUserId);

  int countAllByProjectIdAndTargetUserId(Long projectId, Long targetUserId);

  List<ProjectReview> findAllByProjectIdAndTargetUserId(Long projectId, Long targetUserId);
}
