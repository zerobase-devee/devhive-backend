package com.devee.devhive.domain.user.career.entity.dto;

import com.devee.devhive.domain.user.career.entity.Career;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerDto {

    @NotNull
    @NotBlank
    private String company;
    private String position;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CareerDto careerDto = (CareerDto) o;
        return Objects.equals(company, careerDto.company) &&
            Objects.equals(position, careerDto.position) &&
            Objects.equals(startDate, careerDto.startDate) &&
            Objects.equals(endDate, careerDto.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, position, startDate, endDate);
    }

    public static CareerDto from(Career career) {
        CareerDtoBuilder builder = CareerDto.builder()
            .company(career.getCompany())
            .position(career.getPosition())
            .startDate(career.getStartDate());

        if (career.getEndDate() != null) {
            builder.endDate(career.getEndDate());
        }

        return builder.build();
    }
}
