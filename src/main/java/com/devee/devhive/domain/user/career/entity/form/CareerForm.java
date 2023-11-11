package com.devee.devhive.domain.user.career.entity.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerForm {
    @NotNull
    @NotBlank
    private String company;
    @NotNull
    @NotBlank
    private String position;
    @NotNull
    @NotBlank
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
