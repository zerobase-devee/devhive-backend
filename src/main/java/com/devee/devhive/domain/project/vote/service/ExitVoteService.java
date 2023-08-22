package com.devee.devhive.domain.project.vote.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_REGISTERED_VOTE;
import static com.devee.devhive.global.exception.ErrorCode.ALREADY_SUBMIT_VOTE;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_VOTE;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.repository.ProjectMemberExitVoteRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.exception.ErrorCode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExitVoteService {

  private final ProjectMemberExitVoteRepository exitVoteRepository;

  public String createExitVote(Project project, User registeringUser, User targetUser,
      List<ProjectMember> votingUsers) {
    if (exitVoteRepository.existsByProjectIdAndVoterUserIdAndTargetUserId(project.getId(),
        registeringUser.getId(), targetUser.getId())) {
      throw new CustomException(ALREADY_REGISTERED_VOTE);
    }

    Instant currentTime = Instant.now();
    List<ProjectMemberExitVote> exitVoteList = new ArrayList<>();

    for (ProjectMember member : votingUsers) {
      ProjectMemberExitVote exitVote = ProjectMemberExitVote.of(project, targetUser,
          member.getUser(), currentTime);
      // 등록자의 투표는 자동으로 참여 및 찬성으로 처리
      if (exitVote.getVoterUser().getId().equals(registeringUser.getId())) {
        exitVote.setAccept(true);
        exitVote.setVoted(true);
      }
      exitVoteList.add(exitVote);
    }

//    List<ProjectMemberExitVote> exitVoteList = votingUsers.stream()
//        .map(member -> ProjectMemberExitVote.of(project, targetUser, member.getUser(), currentTime))
//        .collect(Collectors.toList());

    exitVoteRepository.saveAllAndFlush(exitVoteList);

    return targetUser.getNickName() + " 유저에 대한 퇴출 투표 생성이 완료되었습니다.";
  }
}
