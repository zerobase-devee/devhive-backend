package com.devee.devhive.domain.project.chat.service;

import com.devee.devhive.domain.project.chat.entity.ProjectChatMember;
import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.repository.ProjectChatMemberRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

  private final ProjectChatMemberRepository chatMemberRepository;

  public List<ProjectChatMember> findAllByUserId(Long userId) {
    return chatMemberRepository.findAllByUserId(userId);
  }

  public boolean isMemberOfChat(Long roomId, Long userId) {
    return chatMemberRepository.existsByChatRoomIdAndUserId(roomId, userId);
  }

  public ProjectChatMember findMember(Long roomId, Long userId) {
    return chatMemberRepository.findByChatRoomIdAndUserId(roomId, userId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHATMEMBER));
  }

  public String enterChatRoom(ProjectChatRoom room, User user) {
    ProjectChatMember newMember = ProjectChatMember.builder()
        .chatRoom(room)
        .user(user)
        .build();

    chatMemberRepository.save(newMember);

    return room.getTitle() + " 채팅방에 참여합니다.";
  }

  public String exitChatRoom(ProjectChatRoom room, ProjectChatMember member) {
    chatMemberRepository.delete(member);

    return room.getTitle() + " 채팅방에서 퇴장합니다.";
  }
}
