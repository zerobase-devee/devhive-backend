package com.devee.devhive.domain.project.comment.reply.entity.dto;

import com.devee.devhive.domain.project.comment.reply.entity.Reply;
import com.devee.devhive.domain.user.entity.dto.SimpleUserDto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDto {

    private Long replyId;
    private SimpleUserDto userDto;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public static ReplyDto from(Reply reply) {
        return ReplyDto.builder()
            .replyId(reply.getId())
            .userDto(SimpleUserDto.from(reply.getUser()))
            .content(reply.getContent())
            .createDate(reply.getCreatedDate())
            .modifyDate(reply.getModifiedDate())
            .build();
    }
}
