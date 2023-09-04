package com.devee.devhive.domain.user.techstack.entity;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.entity.BaseEntity;
import com.devee.devhive.domain.techstack.entity.TechStack;
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
public class UserTechStack extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_stack_id")
    private TechStack techStack;

    public static UserTechStack of(User user, TechStack techStack) {
        return UserTechStack.builder()
            .user(user)
            .techStack(techStack)
            .build();
    }
}