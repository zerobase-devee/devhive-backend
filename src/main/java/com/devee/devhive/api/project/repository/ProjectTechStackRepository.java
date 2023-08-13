package com.devee.devhive.api.project.repository;

import com.devee.devhive.api.project.entity.ProjectTechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTechStackRepository extends JpaRepository<ProjectTechStack, Long> {

}
