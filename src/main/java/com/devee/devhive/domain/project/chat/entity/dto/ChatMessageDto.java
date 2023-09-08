package com.devee.devhive.domain.project.chat.entity.dto;


import com.devee.devhive.domain.project.chat.entity.ProjectChatMessage;
import com.devee.devhive.domain.project.type.ChatMessageType;
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

  private Long chatRoomId;
  private Long userId;  // 보낸 유저
  private String text;
  private LocalDateTime sendTime;
  private ChatMessageType messageType;

  public static ChatMessageDto from(ProjectChatMessage message) {
    return ChatMessageDto.builder()
        .chatRoomId(message.getProjectChatRoom().getId())
        .userId(message.getSenderUser().getId())
        .text(message.getText())
        .sendTime(message.getCreatedDate())
        .messageType(message.getMessageType())
        .build();
  }
}
