package com.devee.devhive.domain.project.vote.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_SUBMIT_VOTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.repository.ProjectMemberExitVoteRepository;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

class ExitVoteServiceTest {

  @InjectMocks
  private ExitVoteService exitVoteService;
  @Mock
  private ProjectMemberExitVoteRepository exitVoteRepository;
  @Mock
  private ApplicationEventPublisher eventPublisher;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("투표 생성 - 성공")
  void testCreateExitVote() {
    // given
    Project project = Project.builder()
        .id(1L)
        .build();

    User registeringUser = User.builder().id(1L).build();
    User targetUser = User.builder().id(2L).build();
    User otherUser = User.builder().id(3L).build();
    List<User> userList = List.of(registeringUser, targetUser, otherUser);

    List<ProjectMember> projectMemberList = userList.stream()
        .map(user -> ProjectMember.builder()
            .id(user.getId())
            .user(user)
            .project(project)
            .user(user)
            .leader(user.getId() == 1)
            .build()
        ).collect(Collectors.toList());

    Instant currentTime = Instant.now();
    List<ProjectMemberExitVote> exitVoteList = new ArrayList<>();

    for (ProjectMember member : projectMemberList) {
      ProjectMemberExitVote exitVote = ProjectMemberExitVote.of(project, targetUser,
          member.getUser(), currentTime);
      if (exitVote.getVoterUser().getId().equals(registeringUser.getId())) {
        exitVote.setAccept(true);
        exitVote.setVoted(true);
      }
      exitVoteList.add(exitVote);
    }

    when(exitVoteRepository.saveAllAndFlush(any())).thenReturn(exitVoteList);

    // when
    exitVoteService.createExitVoteAndSendAlarm(project,
        registeringUser, targetUser, projectMemberList);

    // then
    verify(eventPublisher, times(projectMemberList.size()-1))
        .publishEvent(any(AlarmForm.class));
  }

  @Test
  @DisplayName("투표 제출 - 성공")
  void testSubmitExitVote() {
    // given
    Project project = Project.builder()
        .id(1L)
        .build();
    User user = User.builder().id(1L).build();
    User targetUser = User.builder().id(2L).build();

    ProjectMemberExitVote myVote = ProjectMemberExitVote.builder()
        .id(1L)
        .project(project)
        .voterUser(user)
        .targetUser(targetUser)
        .isVoted(false)
        .isAccept(true)
        .build();

    when(exitVoteRepository.save(any())).thenReturn(myVote);
    when(exitVoteRepository.findByProjectIdAndVoterUserIdAndTargetUserId
        (anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(myVote));

    // when
    ProjectMemberExitVote savedVote = exitVoteService
        .submitExitVote(project, user, targetUser, true);

    assertThat(savedVote).isNotNull();
    assertThat(savedVote.isVoted()).isTrue();
    assertThat(savedVote.isAccept()).isTrue();
  }

  @Test
  @DisplayName("투표 제출 실패 - 이미 제출한 투표")
  void testSubmitExitVoteFail_alreadySubmit() {
    // given
    Project project = Project.builder()
        .id(1L)
        .build();
    User user = User.builder().id(1L).build();
    User targetUser = User.builder().id(2L).build();

    ProjectMemberExitVote myVote = ProjectMemberExitVote.builder()
        .id(1L)
        .project(project)
        .voterUser(user)
        .targetUser(targetUser)
        .isVoted(true)
        .isAccept(false)
        .build();

    when(exitVoteRepository.save(any())).thenReturn(myVote);
    when(exitVoteRepository.findByProjectIdAndVoterUserIdAndTargetUserId
        (anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(myVote));

    // when
    CustomException exception = assertThrows(CustomException.class,
        () -> exitVoteService
            .submitExitVote(project, user, targetUser, true));

    assertEquals(ALREADY_SUBMIT_VOTE, exception.getErrorCode());
  }
}