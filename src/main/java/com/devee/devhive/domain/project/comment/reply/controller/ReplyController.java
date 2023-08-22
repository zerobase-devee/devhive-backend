package com.devee.devhive.domain.project.comment.reply.controller;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.reply.entity.Reply;
import com.devee.devhive.domain.project.comment.reply.entity.dto.ReplyDto;
import com.devee.devhive.domain.project.comment.reply.entity.form.ReplyForm;
import com.devee.devhive.domain.project.comment.reply.service.ReplyService;
import com.devee.devhive.domain.project.comment.service.CommentService;
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
@RequestMapping("/api/reply")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;
    private final CommentService commentService;


    // 대댓글 생성
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ReplyDto> create(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("commentId") Long commentId,
        @RequestBody @Valid ReplyForm form
    ) {
        User user = principalDetails.getUser();
        Comment comment = commentService.getCommentById(commentId);
        Reply reply = replyService.create(user, comment, form);
        return ResponseEntity.ok(ReplyDto.of(reply, user));
    }

    // 대댓글 수정
    @PutMapping("/{replyId}")
    public ResponseEntity<ReplyDto> update(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("replyId") Long replyId,
        @RequestBody @Valid ReplyForm form
    ) {
        User user = principalDetails.getUser();
        Reply reply = replyService.update(user, replyId, form);
        return ResponseEntity.ok(ReplyDto.of(reply, user));
    }

    // 대댓글 삭제
    @DeleteMapping("/{replyId}")
    public void delete(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable("replyId") Long replyId
    ) {
        User user = principalDetails.getUser();
        replyService.delete(user, replyId);
    }
}
