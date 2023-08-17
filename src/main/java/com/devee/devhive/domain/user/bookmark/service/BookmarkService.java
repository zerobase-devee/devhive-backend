package com.devee.devhive.domain.user.bookmark.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_PROJECT;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.domain.user.bookmark.entity.Bookmark;
import com.devee.devhive.domain.user.bookmark.entity.dto.BookmarkProjectDto;
import com.devee.devhive.domain.user.bookmark.repository.BookmarkRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProjectRepository projectRepository;

    public void register(Authentication authentication, Long projectId) {
        User user = (User) authentication.getPrincipal();
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        bookmarkRepository.save(Bookmark.builder()
                .project(project)
                .user(user)
                .build());
    }

    public void delete(Authentication authentication, Long projectId) {
        User user = (User) authentication.getPrincipal();
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        bookmarkRepository.findBookmarkByUserAndProject(user, project)
            .ifPresent(bookmarkRepository::delete);
    }

    public Page<BookmarkProjectDto> getBookmarkProjects(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();

        return bookmarkRepository.findByUserOrderByCreatedDateDesc(user, pageable)
            .map(bookmark -> BookmarkProjectDto.from(bookmark.getProject()));
    }
}
