package com.devee.devhive.domain.project.chat.service;

import com.devee.devhive.domain.project.chat.entity.ProjectChatMessage;
import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.entity.dto.ChatMessageDto;
import com.devee.devhive.domain.project.chat.repository.ProjectChatMessageRepository;
import com.devee.devhive.domain.project.type.ChatMessageType;
import com.devee.devhive.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ProjectChatMessageRepository chatMessageRepository;

  public ChatMessageDto addMessage(ProjectChatRoom chatRoom, User senderUser,
      String text, ChatMessageType messageType) {
    ProjectChatMessage newMessage = ProjectChatMessage.builder()
        .projectChatRoom(chatRoom)
        .senderUser(senderUser)
        .text(text)
        .messageType(messageType)
        .build();

    chatMessageRepository.save(newMessage);

    return ChatMessageDto.from(newMessage);
  }

  public List<ProjectChatMessage> findByChatRoomId(Long projectChatRoomId) {
    return chatMessageRepository.findAllByProjectChatRoomId(projectChatRoomId);
  }

  public void deleteOfChatRoom(Long projectChatRoomId) {
    List<ProjectChatMessage> chatMembers = findByChatRoomId(projectChatRoomId);
    chatMessageRepository.deleteAll(chatMembers);
  }
}
