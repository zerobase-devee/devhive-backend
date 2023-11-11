package com.devee.devhive.domain.project.views.service;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.views.entity.ViewCount;
import com.devee.devhive.domain.project.views.repository.ViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewCountService {

  private final ViewCountRepository viewCountRepository;

  public void create(Project project) {
    viewCountRepository.save(ViewCount.builder().project(project).count(0).build());
  }

  public void incrementViewCount(Project project) {
    ViewCount viewCount = project.getViewCount();

    viewCount.incrementCount();
    viewCountRepository.save(viewCount);
  }

  public void delete(Long projectId) {
    ViewCount viewCount = viewCountRepository.findByProjectId(projectId);
    viewCountRepository.delete(viewCount);
  }
}
