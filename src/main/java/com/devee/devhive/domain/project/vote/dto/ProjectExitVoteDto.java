package com.devee.devhive.domain.project.vote.dto;

import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectExitVoteDto {
  private Long voteId;
  private Long userId;
  private Long targetUserId;
  private boolean isVoted;
  private Instant createDate;

  public static ProjectExitVoteDto from(ProjectMemberExitVote memberExitVote) {
    return ProjectExitVoteDto.builder()
        .voteId(memberExitVote.getId())
        .userId(memberExitVote.getVoterUser().getId())
        .targetUserId(memberExitVote.getTargetUser().getId())
        .isVoted(memberExitVote.isVoted())
        .createDate(memberExitVote.getCreatedDate())
        .build();
  }
}
