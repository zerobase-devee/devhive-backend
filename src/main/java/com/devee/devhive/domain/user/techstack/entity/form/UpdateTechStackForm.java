package com.devee.devhive.domain.user.techstack.entity.form;

import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTechStackForm {
  private List<TechStackDto> techStacks;
}
