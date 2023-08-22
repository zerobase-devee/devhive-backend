package com.devee.devhive.domain.project.vote.service;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.repository.ProjectMemberExitVoteRepository;
import com.devee.devhive.domain.user.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExitVoteService {

  private final ProjectMemberExitVoteRepository exitVoteRepository;

  public String createExitVote(Project project, User targetUser,
      List<ProjectMember> votingUsers) {
    Instant currentTime = Instant.now();

    List<ProjectMemberExitVote> exitVoteList = votingUsers.stream()
        .map(member -> ProjectMemberExitVote.of(project, targetUser, member.getUser(), currentTime))
        .collect(Collectors.toList());

    exitVoteRepository.saveAll(exitVoteList);

    return targetUser.getNickName() + " 유저에 대한 퇴출 투표 생성이 완료되었습니다.";
  }

}
