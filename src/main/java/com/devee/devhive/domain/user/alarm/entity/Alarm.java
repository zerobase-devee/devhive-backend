package com.devee.devhive.domain.user.alarm.entity;

import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long projectId;
    private String projectName;

    private Long args; // 관심유저아이디 or 타겟유저아이디

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmContent content;

    public static Alarm from(AlarmForm form) {
        User otherUser = form.getUser();
        return Alarm.builder()
            .user(form.getReceiverUser())
            .projectId(form.getProjectId())
            .projectName(form.getProjectName())
            .args(otherUser == null ? null : otherUser.getId())
            .content(form.getContent())
            .build();
    }
}
