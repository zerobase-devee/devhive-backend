package com.devee.devhive.domain.user.career.entity;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Career {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String company;
    private String position;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static Career of(User user, CareerDto careerDto) {
        return Career.builder()
            .user(user)
            .company(careerDto.getCompany())
            .position(careerDto.getPosition())
            .startDate(careerDto.getStartDate())
            .endDate(careerDto.getEndDate())
            .build();
    }
}
