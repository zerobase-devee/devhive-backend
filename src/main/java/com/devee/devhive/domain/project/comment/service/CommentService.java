package com.devee.devhive.domain.project.comment.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_COMMENT;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.entity.form.CommentForm;
import com.devee.devhive.domain.project.comment.repository.CommentRepository;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.redis.RedisService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final RedisService redisService;

  public Comment getCommentById(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));
  }

  // 댓글 생성
  @Transactional
  public Comment create(User user, Project project, CommentForm form) {
    String KEY = "COMMENT_" + project.getId();
    int retryDelayMilliseconds = 200; // 재시도 간격 (예: 200ms)

    while (true) {
      boolean locked = redisService.getLock(KEY, 5);
      if (locked) {
        try {
          return commentRepository.save(Comment.builder()
              .project(project)
              .user(user)
              .content(form.getContent())
              .build());
        } finally {
          redisService.unLock(KEY);
        }
      } else {
        // 락을 획득하지 못한 경우, 일정 시간 동안 대기한 후 재시도
        try {
          Thread.sleep(retryDelayMilliseconds);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
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

  public List<Long> deleteCommentsByProjectId(Long projectId) {
    List<Comment> comments = commentRepository.findAllByProjectId(projectId);
    commentRepository.deleteAll(comments);

    return comments.stream().map(Comment::getId).collect(Collectors.toList());
  }
}
