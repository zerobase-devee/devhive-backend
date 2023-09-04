package com.devee.devhive.domain.project.repository.custom;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.type.DevelopmentType;
import com.devee.devhive.domain.project.type.RecruitmentType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomProjectRepository {

  Page<Project> getProject(String keyword, DevelopmentType development, RecruitmentType recruitment,
      List<Long> techStackIds, String sort, Pageable pageable);
}
