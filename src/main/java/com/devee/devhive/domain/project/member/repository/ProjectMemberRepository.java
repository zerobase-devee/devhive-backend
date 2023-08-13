package com.devee.devhive.domain.project.member.repository;

import com.devee.devhive.domain.project.member.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

}
