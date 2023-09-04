package com.devee.devhive.domain.project.vote.repository;

import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberExitVoteRepository extends JpaRepository<ProjectMemberExitVote, Long> {

  List<ProjectMemberExitVote> findAllByProjectId(Long projectId);
  boolean existsByProjectId(Long projectId);

  Optional<ProjectMemberExitVote> findByProjectIdAndVoterUserIdAndTargetUserId(Long projectId, Long voterUserId, Long targetUserId);

  List<ProjectMemberExitVote> findAllByCreatedDateBefore(Instant createdDate);
}
