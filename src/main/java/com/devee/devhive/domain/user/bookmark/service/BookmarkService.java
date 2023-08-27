package com.devee.devhive.domain.user.bookmark.service;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.bookmark.entity.Bookmark;
import com.devee.devhive.domain.user.bookmark.repository.BookmarkRepository;
import com.devee.devhive.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;

  public void register(User user, Project project) {
    bookmarkRepository.save(Bookmark.builder()
        .project(project)
        .user(user)
        .build());
  }

  public void delete(Long userId, Long projectId) {
    bookmarkRepository.findBookmarkByUserIdAndProjectId(userId, projectId)
        .ifPresent(bookmarkRepository::delete);
  }

  // 북마크 목록 조회
  public Page<Bookmark> getBookmarkProjects(Long userId, Pageable pageable) {
    return bookmarkRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
  }
}
