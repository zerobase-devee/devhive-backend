package com.devee.devhive.domain.project.review.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_SUBMIT_TARGETUSER;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_PROJECT;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.devee.devhive.global.exception.ErrorCode.PROJECT_NOT_COMPLETE;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.domain.project.review.dto.EvaluationForm;
import com.devee.devhive.domain.project.review.dto.ReviewDto;
import com.devee.devhive.domain.project.review.entity.ProjectReview;
import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import com.devee.devhive.domain.project.review.evaluation.repository.EvaluationRepository;
import com.devee.devhive.domain.project.review.repository.ProjectReviewRepository;
import com.devee.devhive.domain.project.type.EvaluationItem;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectReviewService {

  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;
  private final ProjectReviewRepository projectReviewRepository;
  private final EvaluationRepository evaluationRepository;

  // 프로젝트에서 유저가 받은 리뷰의 평균점수
  public double getAverageTotalScoreByTargetUserAndProject(Long targetUserId, Long projectId) {
    return projectReviewRepository.getAverageTotalScoreByTargetUserIdAndProjectId(targetUserId,
        projectId);
  }

  // 정보를 바탕으로 리뷰 등록
  public ReviewDto submitReview(User user, Long projectId, Long targetUserId,
      EvaluationForm form) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

    // 리뷰는 프로젝트가 완료된 상태에서만 작성 가능
    if (project.getStatus() != ProjectStatus.COMPLETE) {
      throw new CustomException(PROJECT_NOT_COMPLETE);
    }

    User targetUser = userRepository.findById(targetUserId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

    if (projectReviewRepository.existsByProjectAndTargetUser(project, targetUser)) {
      throw new CustomException(ALREADY_SUBMIT_TARGETUSER);
    }

    ProjectReview newReview = ProjectReview.builder()
        .project(project)
        .reviewerUser(user)
        .targetUser(targetUser)
        .totalScore(form.getTotalScore())
        .build();

    projectReviewRepository.saveAndFlush(newReview);

    List<Evaluation> evaluationList = getEvaluationList(newReview, form);

    evaluationRepository.saveAll(evaluationList);

    return ReviewDto.of(newReview, evaluationList);
  }

  private List<Evaluation> getEvaluationList(ProjectReview review, EvaluationForm form) {
    return getAllEvaluations(review, form);
  }

  private List<Evaluation> getAllEvaluations(ProjectReview review,
      EvaluationForm form) {
    List<Evaluation> evaluationList = new ArrayList<>();

    evaluationList.add(getEvaluation(review, EvaluationItem.MANNER, form.getManner()));
    evaluationList.add(getEvaluation(review, EvaluationItem.CONTRIBUTION, form.getContribution()));
    evaluationList.add(
        getEvaluation(review, EvaluationItem.COMMUNICATION, form.getCommunication()));
    evaluationList.add(getEvaluation(review, EvaluationItem.SCHEDULE, form.getSchedule()));
    evaluationList.add(
        getEvaluation(review, EvaluationItem.PROFESSIONALISM, form.getProfessionalism()));

    return evaluationList;
  }

  private Evaluation getEvaluation(ProjectReview review, EvaluationItem evaluationItem, int point) {
    return Evaluation.builder()
        .projectReview(review)
        .evaluationItem(evaluationItem)
        .point(point)
        .build();
  }
}
