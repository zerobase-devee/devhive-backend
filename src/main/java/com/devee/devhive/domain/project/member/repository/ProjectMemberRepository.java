package com.devee.devhive.domain.project.member.repository;

import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.repository.custom.CustomProjectMemberRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends
    JpaRepository<ProjectMember, Long>, CustomProjectMemberRepository {

    List<ProjectMember> findAllByProjectId(Long projectId);

    List<ProjectMember> findAllByUserIdOrderByCreatedDateDesc(Long userId);

    Page<ProjectMember> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

    int countAllByProjectId(Long projectId);
}
