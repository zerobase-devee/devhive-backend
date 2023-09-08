package com.devee.devhive.domain.project.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectImageDto {
  private String url;

  public static ProjectImageDto from(String url) {
    return ProjectImageDto.builder()
        .url(url)
        .build();
  }
}
