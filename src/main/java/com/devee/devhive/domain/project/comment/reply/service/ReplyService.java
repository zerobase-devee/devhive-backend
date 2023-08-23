package com.devee.devhive.domain.project.comment.reply.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_REPLY;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.reply.entity.Reply;
import com.devee.devhive.domain.project.comment.reply.entity.form.ReplyForm;
import com.devee.devhive.domain.project.comment.reply.repository.ReplyRepository;
import com.devee.devhive.domain.user.alarm.entity.dto.AlarmProjectDto;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.RelatedUrlType;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.redis.RedisService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {
  private final ApplicationEventPublisher eventPublisher;

  private final ReplyRepository replyRepository;
  private final RedisService redisService;

  public Reply getReplyById(Long replyId) {
    return replyRepository.findById(replyId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));
  }

  // 대댓글 생성
  @Transactional
  public Reply create(User user, Comment comment, ReplyForm form) {
    String KEY = "REPLY_" + comment.getId();
    int retryDelayMilliseconds = 200; // 재시도 간격 (예: 200ms)

    while (true) {
      boolean locked = redisService.getLock(KEY, 5);
      if (locked) {
        try {
          Reply saveReply = replyRepository.save(Reply.builder()
              .comment(comment)
              .user(user)
              .content(form.getContent())
              .build());

          // 댓글 작성자에게 대댓글 알림 이벤트 발행
          AlarmForm alarmForm = AlarmForm.builder()
              .receiverUser(comment.getUser())
              .projectDto(AlarmProjectDto.of(comment.getProject(), RelatedUrlType.PROJECT_POST))
              .build();
          eventPublisher.publishEvent(alarmForm);

          return saveReply;
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

  // 대댓글 수정
  public Reply update(User user, Long replyId, ReplyForm form) {
    Reply reply = getReplyById(replyId);

    if (!Objects.equals(reply.getUser().getId(), user.getId())) {
      throw new CustomException(UNAUTHORIZED);
    }
    reply.setContent(form.getContent());
    return replyRepository.save(reply);
  }

  // 대댓글 삭제
  public void delete(User user, Long replyId) {
    Reply reply = getReplyById(replyId);

    if (!Objects.equals(reply.getUser().getId(), user.getId())) {
      throw new CustomException(UNAUTHORIZED);
    }
    replyRepository.delete(reply);
  }

  public void deleteRepliesByCommentId(Long commentId) {
    List<Reply> repliesToDelete = replyRepository.findAllByCommentId(commentId);
    replyRepository.deleteAll(repliesToDelete);
  }

  public void deleteRepliesByCommentList(List<Long> commentIdList) {
    for (Long commentId : commentIdList) {
      List<Reply> repliesToDelete = replyRepository.findAllByCommentId(commentId);
      replyRepository.deleteAll(repliesToDelete);
    }
  }
}
