package com.devee.devhive.domain.project.member.service;

import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.repository.ProjectMemberRepository;
import com.devee.devhive.domain.user.entity.dto.ProjectHistoryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    public List<ProjectMember> getProjectMemberByProjectId(Long projectId) {
        return projectMemberRepository.findAllByProjectId(projectId);
    }

    public int countCompletedProjectsByUserId(Long userId) {
        return projectMemberRepository.countCompletedProjectsByUserId(userId);
    }

    // 내가 참여한 완료된 프로젝트 이력 정보(프로젝트명, 프로젝트 리뷰 평균 점수)
    public List<ProjectHistoryDto> getProjectNamesAndAverageReviewScoreByUserId(Long userId) {
        return projectMemberRepository.getProjectNamesAndAverageReviewScoreByUserId(userId);
    }

    // 내가 참여한 프로젝트 목록 페이지
    public Page<ProjectMember> getParticipationProjects(Long userId, Pageable pageable) {
        return projectMemberRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
    }
}
