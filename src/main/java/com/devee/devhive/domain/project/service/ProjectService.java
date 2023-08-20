package com.devee.devhive.domain.project.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_PROJECT;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Project findById(Long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));
    }

    // 내가 생성한 프로젝트 목록 페이지
    public Page<Project> getWriteProjects(Long userId, Pageable pageable) {
        return projectRepository.findByWriterUserIdOrderByCreatedDateDesc(userId, pageable);

    }
}
