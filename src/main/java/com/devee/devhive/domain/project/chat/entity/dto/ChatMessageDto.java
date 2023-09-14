package com.devee.devhive.domain.project.chat.entity.dto;


import com.devee.devhive.domain.project.chat.entity.ProjectChatMessage;
import com.devee.devhive.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

  private Long userId;  // 보낸 유저
  private String userNickname;
  private String text;
  private LocalDateTime sendTime;

  public static ChatMessageDto from(ProjectChatMessage message) {
    User senderUser = message.getSenderUser();
    return ChatMessageDto.builder()
        .userId(senderUser.getId())
        .userNickname(senderUser.getNickName())
        .text(message.getText())
        .sendTime(message.getCreatedDate())
        .build();
  }
}
