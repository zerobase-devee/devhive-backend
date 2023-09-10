package com.devee.devhive.domain.user.bookmark.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_BOOKMARK;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.bookmark.entity.Bookmark;
import com.devee.devhive.domain.user.bookmark.repository.BookmarkRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;

  public Bookmark findByUserIdAndProjectId(Long userId, Long projectId) {
    return bookmarkRepository.findBookmarkByUserIdAndProjectId(userId, projectId)
        .orElse(null);
  }

  public Bookmark findById(Long bookmarkId) {
    return bookmarkRepository.findById(bookmarkId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_BOOKMARK));
  }

  public void register(User user, Project project) {
    Bookmark bookmark = findByUserIdAndProjectId(user.getId(), project.getId());
    if (bookmark == null) {
      bookmarkRepository.save(Bookmark.builder()
          .project(project)
          .user(user)
          .build());
    }
  }

  public void delete(Bookmark bookmark) {
    bookmarkRepository.delete(bookmark);
  }

  // 북마크 목록 조회
  public Page<Bookmark> getBookmarkProjects(Long userId, Pageable pageable) {
    return bookmarkRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
  }
}
