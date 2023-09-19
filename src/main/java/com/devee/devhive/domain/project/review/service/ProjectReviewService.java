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
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectReviewService {

  private final ProjectReviewRepository projectReviewRepository;

  // 프로젝트에서 유저가 받은 리뷰의 평균점수
  public Double getAverageTotalScoreByTargetUserAndProject(Long targetUserId, Long projectId, int memberCount) {
    List<ProjectReview> projectReviews = getAllProjectReviewsById(projectId);
    List<ProjectReview> userReviews = projectReviews.stream()
        .filter(projectReview -> Objects.equals(projectReview.getTargetUser().getId(), targetUserId)).toList();

    int count = userReviews.size();
    if (memberCount -1 != count) {
      return null;
    }

    int sumTotalScore = userReviews.stream().mapToInt(ProjectReview::getTotalScore).sum();
    double average = (double) sumTotalScore / count;

    // 결과를 소수점 첫째 자리까지 반올림
    return Math.round(average * 10.0) / 10.0;
  }

  // 리뷰 했는지
  public boolean isReviewed(Long projectId, Long reviewerUserId, Long targetUserId) {
    return projectReviewRepository.existsByProjectIdAndReviewerUserIdAndTargetUserId(projectId, reviewerUserId, targetUserId);
  }

  // 정보를 바탕으로 리뷰 등록
  public ProjectReview submitReview(Long projectId, Long targetUserId, User user,
      Project project, User targetUser, List<EvaluationForm> forms
  ) {
    // 리뷰는 프로젝트가 완료된 상태에서만 작성 가능
    if (project.getStatus() != ProjectStatus.COMPLETE) {
      throw new CustomException(PROJECT_NOT_COMPLETE);
    }

    if (isReviewed(projectId, user.getId(), targetUserId)) {
      throw new CustomException(ALREADY_SUBMIT_TARGETUSER);
    }
    // 총 합계
    int totalScore = forms.stream().mapToInt(EvaluationForm::getPoint).sum();

    return projectReviewRepository.save(ProjectReview.builder()
            .project(project)
            .reviewerUser(user)
            .targetUser(targetUser)
            .totalScore(totalScore)
            .build());
  }

  public List<ProjectReview> getAllProjectReviewsById(Long projectId) {
    return projectReviewRepository.findAllByProjectId(projectId);
  }
}
