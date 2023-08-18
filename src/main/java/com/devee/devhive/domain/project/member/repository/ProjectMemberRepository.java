package com.devee.devhive.domain.project.member.repository;

import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    // 회원 정보의 프로젝트 참여 이력을 위한 쿼리(프로젝트명, 프로젝트에서 받은 리뷰 총점)
    @Query("SELECT p.name, SUM(pr.totalScore), MAX(p.modifiedDate) " +
        "FROM ProjectMember pm " +
        "JOIN pm.project p " +
        "JOIN ProjectReview pr ON p.id = pr.project.id AND pr.targetUser = :user " +
        "WHERE pm.user = :user AND p.status = 'COMPLETE' " +
        "GROUP BY p.name " +
        "ORDER BY MAX(p.modifiedDate) DESC")
    List<Object[]> getProjectNamesAndTotalScoresByUser(@Param("user") User user);
    // 벌집레벨 = 유저가 참여한 프로젝트 중 완료 상태인 프로젝트의 갯수 를 조회하는 메서드
    @Query("SELECT COUNT(p) FROM ProjectMember pm JOIN pm.project p WHERE pm.user = :user AND p.status = 'COMPLETE'")
    int countCompletedProjectsByUser(@Param("user") User user);

    Page<ProjectMember> findByUserOrderByCreatedDateDesc(User User, Pageable pageable);
}
