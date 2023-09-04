package com.devee.devhive.domain.user.bookmark.repository;

import com.devee.devhive.domain.user.bookmark.entity.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {


  boolean existsByUserIdAndProjectId(Long userId, Long projectId);

  Optional<Bookmark> findBookmarkByUserIdAndProjectId(Long userId, Long projectId);

  Page<Bookmark> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

  List<Bookmark> findByUserId(Long userId);
}
