package com.devee.devhive.domain.project.chat.entity.dto;


import com.devee.devhive.domain.project.chat.entity.ProjectChatMessage;
import com.devee.devhive.domain.project.type.ChatMessageType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

  private Long chatRoomId;
  private String senderNickName;
  private String text;
  private LocalDateTime sendTime;
  private ChatMessageType messageType;

  public static ChatMessageDto from(ProjectChatMessage message) {
    return ChatMessageDto.builder()
        .chatRoomId(message.getProjectChatRoom().getId())
        .senderNickName(message.getSenderUser().getNickName())
        .text(message.getText())
        .sendTime(message.getCreatedDate())
        .messageType(message.getMessageType())
        .build();
  }
}
