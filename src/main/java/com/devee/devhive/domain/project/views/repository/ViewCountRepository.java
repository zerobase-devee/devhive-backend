package com.devee.devhive.domain.project.views.repository;

import com.devee.devhive.domain.project.views.entity.ViewCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewCountRepository extends JpaRepository<ViewCount, Long> {

  ViewCount findByProjectId(Long projectId);
}
