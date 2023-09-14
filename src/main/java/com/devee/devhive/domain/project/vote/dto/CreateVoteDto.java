package com.devee.devhive.domain.project.vote.dto;

import com.devee.devhive.domain.project.entity.Project;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVoteDto {

  private Long projectId;
  private Long targetUserId;
  private boolean leader;
  private int teamSize;

  public static CreateVoteDto of(Project project, Long targetUserId) {
    return CreateVoteDto.builder()
        .projectId(project.getId())
        .targetUserId(targetUserId)
        .leader(Objects.equals(project.getUser().getId(), targetUserId))
        .teamSize(project.getTeamSize())
        .build();
  }
}
