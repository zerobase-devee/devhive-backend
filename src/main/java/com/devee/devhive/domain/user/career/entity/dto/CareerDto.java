package com.devee.devhive.domain.user.career.entity.dto;

import com.devee.devhive.domain.user.career.entity.Career;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerDto {

    private Long careerId;
    private String company;
    private String position;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static CareerDto from(Career career) {
        CareerDtoBuilder builder = CareerDto.builder()
            .careerId(career.getId())
            .company(career.getCompany())
            .position(career.getPosition())
            .startDate(career.getStartDate());

        if (career.getEndDate() != null) {
            builder.endDate(career.getEndDate());
        }

        return builder.build();
    }
}
