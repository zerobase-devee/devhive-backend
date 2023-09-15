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
public class VoteDto {

  private Long projectId;
  private Long targetUserId;
  private boolean leader;

  public static VoteDto of(Project project, Long targetUserId) {
    return VoteDto.builder()
        .projectId(project.getId())
        .targetUserId(targetUserId)
        .leader(Objects.equals(project.getUser().getId(), targetUserId))
        .build();
  }
}
