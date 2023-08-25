package com.devee.devhive.domain.project.chat.service;

import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.repository.ProjectChatRoomRepository;
import com.devee.devhive.domain.project.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ProjectChatRoomRepository chatRoomRepository;

  public boolean existsRoomByProjectId(Long projectId) {
    return chatRoomRepository.existsByProjectId(projectId);
  }

  public ProjectChatRoom createChatRoom(Project project, String title) {
    ProjectChatRoom newRoom = ProjectChatRoom.builder()
        .project(project)
        .title(title)
        .build();

    return chatRoomRepository.save(newRoom);
  }
}
