package com.devee.devhive.domain.project.apply.repository;

import com.devee.devhive.domain.project.apply.entity.ProjectApply;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectApplyRepository extends JpaRepository<ProjectApply, Long> {
    Optional<ProjectApply> findByUserIdAndProjectId(Long userId, Long projectId);

    List<ProjectApply> findAllByProjectId(Long projectId);
}
