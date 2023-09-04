package com.devee.devhive.domain.user.career.entity.form;

import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCareerForm {
    private List<CareerDto> careerDtoList;
}
