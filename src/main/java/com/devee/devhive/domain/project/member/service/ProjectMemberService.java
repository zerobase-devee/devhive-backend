package com.devee.devhive.domain.project.member.service;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.repository.ProjectMemberRepository;
import com.devee.devhive.domain.user.entity.User;
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

    // 유저가 참여한 완료된 프로젝트 갯수 (벌집레벨)
    public int countCompletedProjectsByUserId(Long userId) {
        return projectMemberRepository.countCompletedProjectsByUserId(userId);
    }

    // 유저가 참여한 프로젝트 목록(최신순)
    public List<ProjectMember> findAllByUserId(Long userId) {
        return projectMemberRepository.findAllByUserIdOrderByCreatedDateDesc(userId);
    }

    // 내가 참여한 프로젝트 목록 페이지
    public Page<ProjectMember> getParticipationProjects(Long userId, Pageable pageable) {
        return projectMemberRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
    }

    // 신청자 승인 전 참가인원 체크
    public boolean availableAccept(Project project) {
        int memberNums = projectMemberRepository.countAllByProjectId(project.getId());
        return memberNums < project.getTeamSize();
    }

    // 신청 승인된 유저 멤버 저장
    public void saveProjectMember(User user, Project project) {
        projectMemberRepository.save(ProjectMember.builder()
            .user(user)
            .project(project)
            .isLeader(false)
            .build());
    }

    // 프로젝트 작성자 리더 저장
    public void saveProjectLeader(User user, Project project) {
        projectMemberRepository.save(ProjectMember.builder()
            .user(user)
            .project(project)
            .isLeader(true)
            .build());
    }

    public void deleteProjectMembers(Long projectId){
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(projectId);
        projectMemberRepository.deleteAll(projectMembers);
    }
}
