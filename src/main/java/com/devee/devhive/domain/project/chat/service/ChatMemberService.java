package com.devee.devhive.domain.project.chat.service;

import com.devee.devhive.domain.project.chat.entity.ProjectChatMember;
import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.repository.ProjectChatMemberRepository;
import com.devee.devhive.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

  private final ProjectChatMemberRepository chatMemberRepository;

  public List<ProjectChatMember> findAllByUserId(Long userId) {
    return chatMemberRepository.findAllByUserId(userId);
  }

  public Optional<ProjectChatMember> findMember(Long roomId, Long userId) {
    return chatMemberRepository.findByChatRoomIdAndUserId(roomId, userId);
  }

  public String enterChatRoom(ProjectChatRoom room, User user) {
    Optional<ProjectChatMember> memberOptional = findMember(room.getId(), user.getId());

    if (memberOptional.isEmpty()) {
      chatMemberRepository.save(ProjectChatMember.builder()
          .chatRoom(room)
          .user(user)
          .build());
    }

    return room.getTitle() + " 채팅방에 참여합니다.";
  }

  public String exitChatRoom(ProjectChatRoom room, ProjectChatMember member) {
    chatMemberRepository.delete(member);

    return room.getTitle() + " 채팅방에서 퇴장합니다.";
  }

  public List<ProjectChatMember> findByChatRoomId(Long chatRoomId) {
    return chatMemberRepository.findAllByChatRoomId(chatRoomId);
  }

  public void deleteOfChatRoom(Long chatRoomId) {
    List<ProjectChatMember> chatMembers = findByChatRoomId(chatRoomId);
    chatMemberRepository.deleteAll(chatMembers);
  }
}
