package com.devee.devhive.domain.project.review.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_SUBMIT_TARGETUSER;
import static com.devee.devhive.global.exception.ErrorCode.PROJECT_NOT_COMPLETE;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.repository.ProjectReviewRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectReviewService {

  private final ProjectReviewRepository projectReviewRepository;

  // 프로젝트에서 유저가 받은 리뷰의 평균점수
  public double getAverageTotalScoreByTargetUserAndProject(Long targetUserId, Long projectId) {
    return projectReviewRepository.getAverageTotalScoreByTargetUserIdAndProjectId(targetUserId,
        projectId);
  }

  // 정보를 바탕으로 리뷰 등록
  public ProjectReview submitReview(User user, Project project, User targetUser,
      EvaluationForm form) {
    // 리뷰는 프로젝트가 완료된 상태에서만 작성 가능
    if (project.getStatus() != ProjectStatus.COMPLETE) {
      throw new CustomException(PROJECT_NOT_COMPLETE);
    }

    if (projectReviewRepository.existsByProjectAndTargetUser(project, targetUser)) {
      throw new CustomException(ALREADY_SUBMIT_TARGETUSER);
    }

    ProjectReview newReview = ProjectReview.builder()
        .project(project)
        .reviewerUser(user)
        .targetUser(targetUser)
        .totalScore(form.getTotalScore())
        .build();

    return projectReviewRepository.saveAndFlush(newReview);
  }
}
