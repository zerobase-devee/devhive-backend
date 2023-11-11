package com.devee.devhive.domain.project.chat.repository;

import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectChatRoomRepository extends JpaRepository<ProjectChatRoom, Long> {

  boolean existsByProjectId(Long projectId);

  Optional<ProjectChatRoom> findByProjectId(Long projectId);
}
