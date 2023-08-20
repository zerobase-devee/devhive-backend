package com.devee.devhive.domain.user.bookmark.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.bookmark.entity.dto.BookmarkProjectDto;
import com.devee.devhive.domain.user.bookmark.service.BookmarkService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 - 북마크 프로젝트 목록 조회
 * 프로젝트 북마크 등록/해제
 */
@RestController
@RequestMapping("/api/bookmark/projects")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserService userService;
    private final ProjectService projectService;

    // 프로젝트 북마크 등록
    @PostMapping("/{projectId}")
    public void register(Principal principal, @PathVariable("projectId") Long projectId) {
        User user = userService.getUserByPrincipal(principal);
        Project project = projectService.findById(projectId);
        bookmarkService.register(user, project);
    }

    // 프로젝트 북마크 해제
    @DeleteMapping("/{projectId}")
    public void delete(Principal principal, @PathVariable("projectId") Long projectId) {
        User user = userService.getUserByPrincipal(principal);
        bookmarkService.delete(user.getId(), projectId);
    }

    // 북마크 프로젝트 목록 조회
    @GetMapping
    public ResponseEntity<Page<BookmarkProjectDto>> getBookmarkProjects(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Pageable pageable = PageRequest.of(0, 9);
        return ResponseEntity.ok(
            bookmarkService.getBookmarkProjects(user.getId(), pageable)
                .map(bookmark -> BookmarkProjectDto.from(bookmark.getProject())));
    }
}
