package com.devee.devhive.domain.project.comment.reply.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_REPLY;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.reply.entity.Reply;
import com.devee.devhive.domain.project.comment.reply.entity.form.ReplyForm;
import com.devee.devhive.domain.project.comment.reply.repository.ReplyRepository;
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
public class ReplyService {

  private final ApplicationEventPublisher eventPublisher;
  private final ReplyRepository replyRepository;

  public Reply getReplyById(Long replyId) {
    return replyRepository.findById(replyId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));
  }

  // 대댓글 생성
  @Transactional
  public Reply create(User user, Comment comment, ReplyForm form) {
    Reply saveReply = replyRepository.save(Reply.builder()
        .comment(comment)
        .user(user)
        .content(form.getContent())
        .build());

    // 댓글 작성자에게 대댓글 알림 이벤트 발행
    alarmEventPub(comment.getUser(), comment.getProject());

    return saveReply;
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

  private void alarmEventPub(User user, Project project) {
    AlarmForm alarmForm = AlarmForm.builder()
        .receiverUser(user)
        .project(project)
        .content(AlarmContent.REPLY)
        .build();
    eventPublisher.publishEvent(alarmForm);
  }
}
