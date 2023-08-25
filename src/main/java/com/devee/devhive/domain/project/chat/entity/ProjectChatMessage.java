package com.devee.devhive.domain.project.chat.entity;

import com.devee.devhive.domain.project.type.ChatMessageType;
import com.devee.devhive.global.entity.BaseEntity;
import com.devee.devhive.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProjectChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "project_chat_room_id")
    private ProjectChatRoom projectChatRoom;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "sender_user_id")
    private User senderUser;

    @Column(nullable = false)
    private String text;

    private ChatMessageType messageType;
}
