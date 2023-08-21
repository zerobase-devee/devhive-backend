package com.devee.devhive.domain.project.review.repository;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.repository.custom.CustomProjectReviewRepository;
import com.devee.devhive.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectReviewRepository extends
    JpaRepository<ProjectReview, Long>, CustomProjectReviewRepository {

  boolean existsByProjectAndTargetUser(Project project, User targetUser);
}
