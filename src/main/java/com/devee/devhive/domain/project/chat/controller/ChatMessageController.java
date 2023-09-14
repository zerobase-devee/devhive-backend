package com.devee.devhive.domain.project.chat.controller;

import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.entity.dto.ChatMessageDto;
import com.devee.devhive.domain.project.chat.service.ChatMessageService;
import com.devee.devhive.domain.project.chat.service.ChatRoomService;
import com.devee.devhive.domain.project.type.ChatMessageType;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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

  @MessageMapping("/message/enter/{roomId}/{userId}")
  public void enterMember(@DestinationVariable("roomId") Long roomId,
      @DestinationVariable("userId") Long userId) {
    ProjectChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
    User senderUser = userService.getUserById(userId);
    String enterMessage = senderUser.getNickName() + " 님이 채팅에 참여했습니다.";

    ChatMessageDto messageDto = chatMessageService.addMessage(chatRoom, senderUser,
        enterMessage, ChatMessageType.ENTER);

    sendingTemplate.convertAndSend("/sub/chat/" + chatRoom.getId(), messageDto);
  }

  @MessageMapping("/message/{roomId}")
  public void sendMessage(@Payload ChatMessageDto message,
      @DestinationVariable("roomId") Long roomId) {
    ProjectChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
    User senderUser = userService.getUserById(message.getUserDto().getUserId());

    ChatMessageDto messageDto = chatMessageService.addMessage(chatRoom, senderUser,
        message.getText(), ChatMessageType.TALK);

    sendingTemplate.convertAndSend("/sub/chat/" + chatRoom.getId(), messageDto);
  }

  @MessageMapping("/message/exit/{roomId}/{userId}")
  public void exitMember(@DestinationVariable("roomId") Long roomId,
      @DestinationVariable("userId") Long userId) {
    ProjectChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
    User senderUser = userService.getUserById(userId);
    String exitMessage = senderUser.getNickName() + " 님이 채팅에서 나갔습니다.";

    ChatMessageDto messageDto = chatMessageService.addMessage(chatRoom, senderUser,
        exitMessage, ChatMessageType.EXIT);

    sendingTemplate.convertAndSend("/sub/chat/" + chatRoom.getId(), messageDto);
  }
}
