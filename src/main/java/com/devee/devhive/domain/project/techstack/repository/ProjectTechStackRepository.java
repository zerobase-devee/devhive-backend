package com.devee.devhive.domain.project.techstack.repository;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.techstack.entity.ProjectTechStack;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTechStackRepository extends JpaRepository<ProjectTechStack, Long> {

  List<ProjectTechStack> findAllByProjectId(Long projectId);

  List<ProjectTechStack> findByProject(Project project);
}
