package com.devee.devhive.domain.project.comment.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_COMMENT;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.entity.form.CommentForm;
import com.devee.devhive.domain.project.comment.reply.service.ReplyService;
import com.devee.devhive.domain.project.comment.repository.CommentRepository;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.global.exception.CustomException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final ApplicationEventPublisher eventPublisher;
  private final CommentRepository commentRepository;
  private final ReplyService replyService;

  public Comment getCommentById(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));
  }

  public List<Comment> getCommentsByProjectId(Long projectId) {
    return commentRepository.findAllByProjectIdOrderByCreatedDateAsc(projectId);
  }

  // 댓글 생성
  @Transactional
  public Comment createAndSendAlarmToProjectUser(User user, Project project, CommentForm form) {
    Comment comment = commentRepository.save(Comment.builder()
        .project(project)
        .user(user)
        .content(form.getContent())
        .build());

    // 게시글 작성자에게 댓글 알림 이벤트 발행
    commentAlarmEventPub(project.getUser(), project);
    return comment;
  }

  // 댓글 수정
  public Comment update(User user, Long commentId, CommentForm form) {
    Comment comment = getCommentById(commentId);

    if (!Objects.equals(comment.getUser().getId(), user.getId())) {
      throw new CustomException(UNAUTHORIZED);
    }
    comment.setContent(form.getContent());
    return commentRepository.save(comment);
  }

  // 댓글 삭제
  public void delete(Comment comment) {
    commentRepository.delete(comment);
  }

  public void deleteCommentsByProjectId(Long projectId) {
    List<Comment> comments = getCommentsByProjectId(projectId);
    List<Long> commentIds = comments.stream().map(Comment::getId).toList();
    replyService.deleteRepliesByCommentList(commentIds);
    commentRepository.deleteAll(comments);
  }

  private void commentAlarmEventPub(User user, Project project) {
    AlarmForm alarmForm = AlarmForm.builder()
        .receiverUser(user)
        .projectId(project.getId())
        .projectName(project.getName())
        .content(AlarmContent.COMMENT)
        .build();
    eventPublisher.publishEvent(alarmForm);
  }
}
