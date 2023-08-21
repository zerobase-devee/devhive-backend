package com.devee.devhive.domain.project.comment.reply.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_REPLY;
import static com.devee.devhive.global.exception.ErrorCode.NOT_YOUR_REPLY;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.reply.entity.Reply;
import com.devee.devhive.domain.project.comment.reply.entity.form.ReplyForm;
import com.devee.devhive.domain.project.comment.reply.repository.ReplyRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.redis.RedisService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {

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
                    return replyRepository.save(Reply.builder()
                        .comment(comment)
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

    // 대댓글 수정
    public Reply update(User user, Long replyId, ReplyForm form) {
        Reply reply = getReplyById(replyId);

        if (!Objects.equals(reply.getUser().getId(), user.getId())) {
            throw new CustomException(NOT_YOUR_REPLY);
        }
        reply.setContent(form.getContent());
        return replyRepository.save(reply);
    }

    // 대댓글 삭제
    public void delete(User user, Long commentId) {
        Reply reply = getReplyById(commentId);

        if (!Objects.equals(reply.getUser().getId(), user.getId())) {
            throw new CustomException(NOT_YOUR_REPLY);
        }
        replyRepository.delete(reply);
    }
}
