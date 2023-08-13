package com.devee.devhive.api.project.chat.repository;

import com.devee.devhive.api.project.chat.entity.ProjectChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectChatRoomRepository extends JpaRepository<ProjectChatRoom, Long> {

}
