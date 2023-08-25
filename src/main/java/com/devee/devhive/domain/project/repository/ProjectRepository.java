package com.devee.devhive.domain.project.repository;

import com.devee.devhive.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  Page<Project> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);
}
