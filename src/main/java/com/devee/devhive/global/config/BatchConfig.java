package com.devee.devhive.global.config;


import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.service.ExitVoteService;
import com.devee.devhive.domain.user.exithistory.entity.ExitHistory;
import com.devee.devhive.domain.user.exithistory.service.ExitHistoryService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

  private final ExitVoteService exitVoteService;
  private final ProjectMemberService projectMemberService;
  private final ExitHistoryService exitHistoryService;
  private final ProjectService projectService;

  @Bean
  public Job voteProcessJob(JobRepository jobRepository, Step step) {
    return new JobBuilder("voteProcessJob", jobRepository)
        .start(step)
        .build();
  }

  @Bean
  public Step voteProcessStep(JobRepository jobRepository, Tasklet voteProcessTasklet,
      PlatformTransactionManager platformTransactionManager) {
    return new StepBuilder("voteProcessStep", jobRepository)
        .tasklet(voteProcessTasklet, platformTransactionManager)
        .build();
  }

  @Bean
  public Tasklet voteProcessTasklet() {
    return ((contribution, chunkContext) -> {
      Map<Long, List<ProjectMemberExitVote>> sortedVotesMap
          = exitVoteService.getSortedVotes();

      Map<Long, ExitHistory> exitHistoryMap = exitVoteService
          .processVotes(sortedVotesMap);

      for (Long projectId : exitHistoryMap.keySet()) {
        ExitHistory currentExitHistory = exitHistoryMap.get(projectId);
        Long exitedUserId = currentExitHistory.getUser().getId();

        // 해당 프로젝트의 리더인 경우
        if (projectMemberService.isLeaderOfProject(projectId, exitedUserId)) {
          log.info("해당 프로젝트의 리더입니다. 프로젝트를 삭제합니다.");

          projectMemberService.deleteAllOfMembersFromProject(projectId);
          projectService.deleteLeadersProject(projectId);

        } else {
          projectMemberService.deleteMemberFromProject(projectId, exitedUserId);
        }

        ExitHistory savedExitHistory = exitHistoryService.saveExitHistory(currentExitHistory);

        String exitedUserName = savedExitHistory.getUser().getNickName();
        int exitCount = exitHistoryService.countExitHistoryByUserId(exitedUserId);
        log.info("{} 유저 퇴출 완료. 누적 {}회", exitedUserName, exitCount);
      }

      return RepeatStatus.FINISHED;
    });
  }
}
