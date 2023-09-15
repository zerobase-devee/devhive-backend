package com.devee.devhive.domain.project.chat.repository;

import com.devee.devhive.domain.project.chat.entity.ProjectChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectChatMessageRepository extends JpaRepository<ProjectChatMessage, Long> {

  List<ProjectChatMessage> findAllByProjectChatRoomId(Long projectChatRoomId);
}
