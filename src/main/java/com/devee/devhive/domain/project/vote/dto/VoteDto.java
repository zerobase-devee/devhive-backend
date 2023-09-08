package com.devee.devhive.domain.project.vote.dto;

import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
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
  private Long voterId;
  private Long targetId;
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
