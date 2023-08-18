package com.devee.devhive.domain.user.controller;

import com.devee.devhive.domain.project.entity.dto.MyProjectInfoDto;
import com.devee.devhive.domain.project.entity.dto.SimpleProjectDto;
import com.devee.devhive.domain.user.service.UserProjectService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Page<SimpleProjectDto>> getWriteProjects(Principal principal) {
        Pageable pageable = PageRequest.of(0, 3);
        return ResponseEntity.ok(userProjectService.getWriteProjects(principal, pageable));
    }

    // 내가 참여한 프로젝트 페이지
    @GetMapping("/participation")
    public ResponseEntity<Page<SimpleProjectDto>> getParticipationProjects(Principal principal) {
        Pageable pageable = PageRequest.of(0, 3);
        return ResponseEntity.ok(userProjectService.getParticipationProjects(principal, pageable));
    }

    // 내 프로젝트 정보 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<MyProjectInfoDto> getProjectInfo(
        @PathVariable("projectId") Long projectId, Principal principal
    ) {
        return ResponseEntity.ok(userProjectService.getProjectInfo(projectId, principal));
    }
}
