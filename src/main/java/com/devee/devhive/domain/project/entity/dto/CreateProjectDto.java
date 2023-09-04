package com.devee.devhive.domain.project.entity.dto;

import com.devee.devhive.domain.project.type.DevelopmentType;
import com.devee.devhive.domain.project.type.RecruitmentType;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
  @NotBlank
  @Size(max = 1000)
  private String content;
  private String projectName;
  private int teamSize;
  private RecruitmentType recruitmentType;
  private String region;
  private DevelopmentType developmentType;
  private LocalDateTime deadline;
  private List<TechStackDto> techStacks;
}
