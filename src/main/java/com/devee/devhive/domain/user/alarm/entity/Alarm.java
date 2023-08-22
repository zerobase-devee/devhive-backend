package com.devee.devhive.domain.user.alarm.entity;

import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.global.entity.BaseEntity;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.AlarmContent;
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

    private Long args; // 프로젝트아이디
    private Long args2; // 보낸유저아이디 or 타겟유저아이디
    private AlarmContent content;

    public static Alarm from(AlarmForm form) {
        Alarm.AlarmBuilder builder = Alarm.builder()
            .user(form.getReceiverUser())
            .args(form.getProjectDto().getProjectId())
            .content(form.getContent());

        if (form.getUserDto() != null) {
            builder.args2(form.getUserDto().getUserId());
        }

        return builder.build();
    }
}
