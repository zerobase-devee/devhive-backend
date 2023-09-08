package com.devee.devhive.domain.project.chat.controller;

import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.entity.dto.ChatMessageDto;
import com.devee.devhive.domain.project.chat.service.ChatMessageService;
import com.devee.devhive.domain.project.chat.service.ChatRoomService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

  private final SimpMessageSendingOperations sendingTemplate;
  private final ChatMessageService chatMessageService;
  private final ChatRoomService chatRoomService;
  private final UserService userService;

  @MessageMapping("/message/enter")
  public void enterMember(@Payload ChatMessageDto message) {
    ProjectChatRoom chatRoom = chatRoomService.findByRoomId(message.getChatRoomId());
    User senderUser = userService.getUserById(message.getUserId());

    chatMessageService.addMessage(chatRoom, senderUser,
        message.getText(), message.getMessageType());

    sendingTemplate.convertAndSend("/sub/chat/" + chatRoom.getId(), message);
  }
}
