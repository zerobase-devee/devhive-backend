package com.devee.devhive.domain.user.entity.form;

import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
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
public class UpdateEtcInfoForm {
    private List<TechStackDto> techStacks;
    private List<CareerDto> careerDtoList;
}
