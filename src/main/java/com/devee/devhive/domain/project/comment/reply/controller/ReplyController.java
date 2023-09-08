package com.devee.devhive.domain.project.comment.reply.controller;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.reply.entity.Reply;
import com.devee.devhive.domain.project.comment.reply.entity.dto.ReplyDto;
import com.devee.devhive.domain.project.comment.reply.entity.form.ReplyForm;
import com.devee.devhive.domain.project.comment.reply.service.ReplyService;
import com.devee.devhive.domain.project.comment.service.CommentService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/reply")
@RequiredArgsConstructor
@Tag(name = "REPLY API", description = "답글 API")
public class ReplyController {

    private final ReplyService replyService;
    private final CommentService commentService;
    private final UserService userService;

    // 대댓글 생성
    @PostMapping("/comments/{commentId}")
    @Operation(summary = "답글 작성")
    public ResponseEntity<ReplyDto> create(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("commentId") Long commentId, @RequestBody @Valid ReplyForm form
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        Comment comment = commentService.getCommentById(commentId);
        Reply reply = replyService.createAndSendAlarmToCommentUser(user, comment, form);
        return ResponseEntity.ok(ReplyDto.from(reply));
    }

    // 대댓글 수정
    @PutMapping("/{replyId}")
    @Operation(summary = "답글 수정")
    public ResponseEntity<ReplyDto> update(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("replyId") Long replyId, @RequestBody @Valid ReplyForm form
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        Reply reply = replyService.update(user, replyId, form);
        return ResponseEntity.ok(ReplyDto.from(reply));
    }

    // 대댓글 삭제
    @DeleteMapping("/{replyId}")
    @Operation(summary = "답글 삭제")
    public void delete(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("replyId") Long replyId
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());
        replyService.delete(user, replyId);
    }
}
