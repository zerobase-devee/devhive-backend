package com.devee.devhive.api.project.member.repository;

import com.devee.devhive.api.project.member.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

}
