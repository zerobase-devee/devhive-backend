package com.devee.devhive.domain.project.chat.service;

import com.devee.devhive.domain.project.chat.entity.ProjectChatMember;
import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.repository.ProjectChatMemberRepository;
import com.devee.devhive.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

  private final ProjectChatMemberRepository chatMemberRepository;

  public boolean isMemberOfChat(Long roomId, Long userId) {
    return chatMemberRepository.existsByChatRoomIdAndUserId(roomId, userId);
  }

  public String enterChatRoom(ProjectChatRoom room, User user) {
    ProjectChatMember newMember = ProjectChatMember.builder()
        .chatRoom(room)
        .user(user)
        .build();

    chatMemberRepository.save(newMember);

    return room.getTitle() + " 채팅방에 참여합니다.";
  }
}
