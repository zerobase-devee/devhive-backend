package com.devee.devhive.domain.user.bookmark.controller;

import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.bookmark.entity.Bookmark;
import com.devee.devhive.domain.user.bookmark.entity.dto.BookmarkProjectDto;
import com.devee.devhive.domain.user.bookmark.service.BookmarkService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 - 북마크 프로젝트 목록 조회 프로젝트 북마크 등록/해제
 */
@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
@Tag(name = "BOOKMARK API", description = "북마크 API")
public class BookmarkController {

  private final UserService userService;
  private final BookmarkService bookmarkService;
  private final ProjectService projectService;

  // 프로젝트 북마크 등록
  @PostMapping("/projects/{projectId}")
  @Operation(summary = "프로젝트 북마크 등록")
  public void register(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable("projectId") Long projectId
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    Project project = projectService.findById(projectId);
    bookmarkService.register(user, project);
  }

  // 프로젝트 북마크 해제
  @DeleteMapping("/{bookmarkId}")
  @Operation(summary = "프로젝트 북마크 해제")
  public void delete(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable("bookmarkId") Long bookmarkId
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    Bookmark bookmark = bookmarkService.findById(bookmarkId);
    if (!Objects.equals(user.getId(), bookmark.getUser().getId())) {
      throw new CustomException(UNAUTHORIZED);
    }
    bookmarkService.delete(bookmark);
  }

  // 북마크 프로젝트 목록 조회
  @GetMapping
  @Operation(summary = "북마크 프로젝트 목록 조회")
  public ResponseEntity<Page<BookmarkProjectDto>> getBookmarkProjects(
      @AuthenticationPrincipal PrincipalDetails principal, Pageable pageable
  ) {
    User user = userService.getUserByEmail(principal.getEmail());

    return ResponseEntity.ok(bookmarkService.getBookmarkProjects(user.getId(), pageable)
            .map(BookmarkProjectDto::from)
    );
  }
}
