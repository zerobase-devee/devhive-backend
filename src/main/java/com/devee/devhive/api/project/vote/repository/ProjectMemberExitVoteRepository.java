package com.devee.devhive.api.project.vote.repository;

import com.devee.devhive.api.project.vote.entity.ProjectMemberExitVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberExitVoteRepository extends JpaRepository<ProjectMemberExitVote, Long> {

}
