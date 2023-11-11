package com.devee.devhive.domain.techstack.entity.dto;

import com.devee.devhive.domain.techstack.entity.TechStack;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechStackDto {

  private Long id;
  private String name;
  private String image;

  public TechStackDto(String name) {
    this.name = name;
  }

  public static TechStackDto from(TechStack techStack) {
    if (techStack != null) {
      return TechStackDto.builder()
          .id(techStack.getId())
          .name(techStack.getName())
          .image(techStack.getImage())
          .build();
    }
    return new TechStackDto();
  }
}
