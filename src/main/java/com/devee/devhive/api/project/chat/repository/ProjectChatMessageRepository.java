package com.devee.devhive.api.project.chat.repository;

import com.devee.devhive.api.project.chat.entity.ProjectChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectChatMessageRepository extends JpaRepository<ProjectChatMessage, Long> {

}
