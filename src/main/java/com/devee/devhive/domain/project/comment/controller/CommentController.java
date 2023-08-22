package com.devee.devhive.domain.project.comment.controller;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.entity.dto.CommentDto;
import com.devee.devhive.domain.project.comment.entity.form.CommentForm;
import com.devee.devhive.domain.project.comment.service.CommentService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.security.service.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ProjectService projectService;

    // 댓글 생성
    @PostMapping("/projects/{projectId}")
    public ResponseEntity<CommentDto> create(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId,
        @RequestBody @Valid CommentForm form
    ) {
        User user = principalDetails.getUser();
        Project project = projectService.findById(projectId);
        Comment comment = commentService.create(user, project, form);
        return ResponseEntity.ok(CommentDto.of(comment, user));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("commentId") Long commentId,
        @RequestBody @Valid CommentForm form
    ) {
        User user = principalDetails.getUser();
        Comment comment = commentService.update(user, commentId, form);
        return ResponseEntity.ok(CommentDto.of(comment, user));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public void delete(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("commentId") Long commentId
    ) {
        User user = principalDetails.getUser();
        commentService.delete(user, commentId);
    }
}
