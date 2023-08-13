package com.devee.devhive.domain.project.apply.repository;

import com.devee.devhive.domain.project.apply.entity.ProjectApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectApplyRepository extends JpaRepository<ProjectApply, Long> {

}