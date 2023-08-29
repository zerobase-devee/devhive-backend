package com.devee.devhive.domain.project.comment.entity.dto;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.reply.entity.dto.ReplyDto;
import com.devee.devhive.domain.user.entity.dto.SimpleUserDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentAndReplyDto {

    private Long commentId;
    private SimpleUserDto userDto;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private List<ReplyDto> replies;

    public static CommentAndReplyDto of(Comment comment, List<ReplyDto> replies) {
        return CommentAndReplyDto.builder()
            .commentId(comment.getId())
            .userDto(SimpleUserDto.from(comment.getUser()))
            .content(comment.getContent())
            .createDate(comment.getCreatedDate())
            .modifyDate(comment.getModifiedDate())
            .replies(replies)
            .build();
    }
}
