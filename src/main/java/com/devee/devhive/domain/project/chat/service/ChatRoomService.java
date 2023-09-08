package com.devee.devhive.domain.project.chat.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_CHATROOM;

import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.repository.ProjectChatRoomRepository;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ProjectChatRoomRepository chatRoomRepository;

  public boolean existsRoomByProjectId(Long projectId) {
    return chatRoomRepository.existsByProjectId(projectId);
  }

  public ProjectChatRoom findByRoomId(Long roomId) {
    return chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_CHATROOM));
  }

  public ProjectChatRoom createChatRoom(Project project, String title) {
    ProjectChatRoom newRoom = ProjectChatRoom.builder()
        .project(project)
        .title(title)
        .build();

    return chatRoomRepository.save(newRoom);
  }
}