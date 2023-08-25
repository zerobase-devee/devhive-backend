package com.devee.devhive.domain.project.chat.entity.dto;

import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {

  private Long roomId;
  private Long projectId;
  private String title;

  public static ChatRoomDto from(ProjectChatRoom chatRoom) {
    return ChatRoomDto.builder()
        .roomId(chatRoom.getId())
        .projectId(chatRoom.getProject().getId())
        .title(chatRoom.getTitle())
        .build();
  }
}
