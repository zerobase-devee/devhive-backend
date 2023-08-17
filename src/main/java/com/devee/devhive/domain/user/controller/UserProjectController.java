package com.devee.devhive.domain.user.controller;

import com.devee.devhive.domain.project.entity.dto.MyProjectInfoDto;
import com.devee.devhive.domain.project.entity.dto.SimpleProjectDto;
import com.devee.devhive.domain.user.service.UserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/project")
@RequiredArgsConstructor
public class UserProjectController {

    private final UserProjectService userProjectService;

    // 내가 생성한 프로젝트 페이지
    @GetMapping("/write")
    public ResponseEntity<Page<SimpleProjectDto>> getWriteProjects(Authentication authentication) {
        Pageable pageable = PageRequest.of(1, 3);
        return ResponseEntity.ok(userProjectService.getWriteProjects(authentication, pageable));
    }

    // 내가 참여한 프로젝트 페이지
    @GetMapping("/participation")
    public ResponseEntity<Page<SimpleProjectDto>> getParticipationProjects(Authentication authentication) {
        Pageable pageable = PageRequest.of(1, 3);
        return ResponseEntity.ok(userProjectService.getParticipationProjects(authentication, pageable));
    }

    // 내 프로젝트 정보 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<MyProjectInfoDto> getProjectInfo(
        @PathVariable("projectId") Long projectId, Authentication authentication
    ) {
        return ResponseEntity.ok(userProjectService.getProjectInfo(projectId, authentication));
    }
}
