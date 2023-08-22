package com.devee.devhive.domain.project.comment.entity.dto;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.user.entity.User;
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
public class CommentDto {

    private Long commentId;
    private SimpleUserDto userDto;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public static CommentDto of(Comment comment, User user) {
        return CommentDto.builder()
            .commentId(comment.getId())
            .userDto(SimpleUserDto.from(user))
            .content(comment.getContent())
            .createDate(comment.getCreatedDate())
            .modifyDate(comment.getModifiedDate())
            .build();
    }
}
