package com.devee.devhive.domain.project.entity.dto;

import com.devee.devhive.domain.project.type.DevelopmentType;
import com.devee.devhive.domain.project.type.RecruitmentType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProjectDto {

  private String keyword;
  private DevelopmentType development;
  private RecruitmentType recruitment;
  private List<Long> techStackIds;
}
