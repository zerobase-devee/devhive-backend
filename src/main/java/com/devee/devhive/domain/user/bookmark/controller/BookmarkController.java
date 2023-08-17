package com.devee.devhive.domain.user.bookmark.controller;

import com.devee.devhive.domain.user.bookmark.entity.dto.BookmarkProjectDto;
import com.devee.devhive.domain.user.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmark/projects")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{projectId}")
    public void register(Authentication authentication, @PathVariable("projectId") Long projectId) {
        bookmarkService.register(authentication, projectId);
    }

    @DeleteMapping("/{projectId}")
    public void delete(Authentication authentication, @PathVariable("projectId") Long projectId) {
        bookmarkService.delete(authentication, projectId);
    }

    @GetMapping
    public ResponseEntity<Page<BookmarkProjectDto>> getBookmarkProjects(Authentication authentication) {
        Pageable pageable = PageRequest.of(1, 9);
        return ResponseEntity.ok(bookmarkService.getBookmarkProjects(authentication, pageable));
    }
}
