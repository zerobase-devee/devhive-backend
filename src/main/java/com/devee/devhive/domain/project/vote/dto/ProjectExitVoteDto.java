package com.devee.devhive.domain.project.vote.dto;

import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectExitVoteDto {
  private Long targetUserId;
  private Instant createDate;
  private List<VotedDto> votedDtoList;

  public static ProjectExitVoteDto of(ProjectMemberExitVote memberExitVote, List<VotedDto> votedDtoList) {
    return ProjectExitVoteDto.builder()
        .targetUserId(memberExitVote.getTargetUser().getId())
        .createDate(memberExitVote.getCreatedDate())
        .votedDtoList(votedDtoList)
        .build();
  }

}
