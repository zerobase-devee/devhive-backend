package com.devee.devhive.domain.project.comment.controller;

import static com.devee.devhive.global.exception.ErrorCode.PLEASE_CHANGE_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.entity.dto.CommentAndReplyDto;
import com.devee.devhive.domain.project.comment.entity.dto.CommentDto;
import com.devee.devhive.domain.project.comment.entity.form.CommentForm;
import com.devee.devhive.domain.project.comment.reply.entity.dto.ReplyDto;
import com.devee.devhive.domain.project.comment.reply.service.ReplyService;
import com.devee.devhive.domain.project.comment.service.CommentService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.entity.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "COMMNENT API", description = "댓글 API")
public class CommentController {

    private final UserService userService;
    private final CommentService commentService;
    private final ProjectService projectService;
    private final ReplyService replyService;

    // 댓글 생성
    @PostMapping("/projects/{projectId}")
    @Operation(summary = "프로젝트 댓글 작성")
    public ResponseEntity<CommentDto> create(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("projectId") Long projectId, @RequestBody @Valid CommentForm form
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        if (user.getNickName().startsWith("닉네임변경해주세요")) {
            throw new CustomException(PLEASE_CHANGE_NICKNAME);
        }
        Project project = projectService.findById(projectId);
        Comment saveComment = commentService.createAndSendAlarmToProjectUser(user, project, form);

        return ResponseEntity.ok(CommentDto.from(saveComment));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    @Operation(summary = "프로젝트 댓글 수정")
    public ResponseEntity<CommentDto> update(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("commentId") Long commentId, @RequestBody @Valid CommentForm form
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        Comment comment = commentService.update(user, commentId, form);
        return ResponseEntity.ok(CommentDto.from(comment));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "프로젝트 댓글 삭제")
    public void delete(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("commentId") Long commentId
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        Comment comment = commentService.getCommentById(commentId);
        if (!Objects.equals(comment.getUser().getId(), user.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }
        // 댓글 관련 대댓글 먼저 모두 삭제
        replyService.deleteRepliesByCommentId(commentId);
        commentService.delete(comment);
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "프로젝트 댓글 & 답글")
    public ResponseEntity<List<CommentAndReplyDto>> getCommentAndReplyDtoList(
        @PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(commentService.getCommentsByProjectId(projectId).stream()
            .map(comment -> {
                List<ReplyDto> replies = replyService.getRepliesByCommentId(comment.getId()).stream()
                    .map(ReplyDto::from)
                    .toList();
                return CommentAndReplyDto.of(comment, replies);
            }).toList());
    }
}
