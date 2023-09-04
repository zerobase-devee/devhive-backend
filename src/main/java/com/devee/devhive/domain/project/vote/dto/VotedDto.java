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
public class VotedDto {
  private Long userId;
  private boolean isVoted;

  public static VotedDto from(ProjectMemberExitVote memberExitVote) {
    return VotedDto.builder()
        .userId(memberExitVote.getVoterUser().getId())
        .isVoted(memberExitVote.isVoted())
        .build();
  }
}
