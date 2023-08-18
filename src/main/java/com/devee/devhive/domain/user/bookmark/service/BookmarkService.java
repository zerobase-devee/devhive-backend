package com.devee.devhive.domain.user.bookmark.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_PROJECT;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_USER;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.domain.user.bookmark.entity.Bookmark;
import com.devee.devhive.domain.user.bookmark.entity.dto.BookmarkProjectDto;
import com.devee.devhive.domain.user.bookmark.repository.BookmarkRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.exception.CustomException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public void register(Principal principal, Long projectId) {
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        bookmarkRepository.save(Bookmark.builder()
                .project(project)
                .user(user)
                .build());
    }

    public void delete(Principal principal, Long projectId) {
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        bookmarkRepository.findBookmarkByUserAndProject(user, project)
            .ifPresent(bookmarkRepository::delete);
    }

    public Page<BookmarkProjectDto> getBookmarkProjects(Principal principal, Pageable pageable) {
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        return bookmarkRepository.findByUserOrderByCreatedDateDesc(user, pageable)
            .map(bookmark -> BookmarkProjectDto.from(bookmark.getProject()));
    }
}
