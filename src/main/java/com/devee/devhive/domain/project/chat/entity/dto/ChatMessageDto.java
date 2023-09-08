package com.devee.devhive.domain.project.chat.entity.dto;


import com.devee.devhive.domain.project.chat.entity.ProjectChatMessage;
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
  private String text;
  private LocalDateTime sendTime;

  public static ChatMessageDto from(ProjectChatMessage message) {
    return ChatMessageDto.builder()
        .userId(message.getSenderUser().getId())
        .text(message.getText())
        .sendTime(message.getCreatedDate())
        .build();
  }
}
