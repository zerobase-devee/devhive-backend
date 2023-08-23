package com.devee.devhive.domain.project.vote.dto;

import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteDto {

  @JsonProperty(value = "project_id")
  private long projectId;
  @JsonProperty(value = "voter_id")
  private long voterId;
  @JsonProperty(value = "target_id")
  private long targetId;
  @JsonProperty(value = "vote")
  private boolean vote;

  public static VoteDto from(ProjectMemberExitVote vote) {
    return VoteDto.builder()
        .projectId(vote.getProject().getId())
        .voterId(vote.getVoterUser().getId())
        .targetId(vote.getTargetUser().getId())
        .vote(vote.isAccept())
        .build();
  }
}
