package com.devee.devhive.domain.project.entity.dto;

import com.devee.devhive.domain.project.type.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectStatusDto {

  private ProjectStatus status;
}
