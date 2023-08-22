package com.devee.devhive.domain.project.vote.entity;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberExitVote {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "voter_user_id")
  private User voterUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_user_id")
  private User targetUser;

  private boolean isVoted;
  private boolean isAccept;
  private Instant createdDate;

  public static ProjectMemberExitVote of(Project project, User targetUser, User votingUser,
      Instant currentTime) {
    return ProjectMemberExitVote.builder()
        .project(project)
        .targetUser(targetUser)
        .voterUser(votingUser)
        .isVoted(false)
        .isAccept(false)
        .createdDate(currentTime)
        .build();
  }
}
