package com.devee.devhive.domain.project.entity.dto;

import com.devee.devhive.domain.project.type.DevelopmentType;
import com.devee.devhive.domain.project.type.RecruitmentType;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectDto {

  private String title;
  private String content;
  private String projectName;
  private int teamSize;
  private RecruitmentType recruitmentType;
  private String region;
  private DevelopmentType developmentType;
  private LocalDateTime deadline;
  private List<TechStackDto> techStacks;
}
