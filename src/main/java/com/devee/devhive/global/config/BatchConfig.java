package com.devee.devhive.global.config;


import com.devee.devhive.domain.project.vote.entity.ProjectMemberExitVote;
import com.devee.devhive.domain.project.vote.service.ExitVoteService;
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

      exitVoteService.processVotes(sortedVotesMap);

      return RepeatStatus.FINISHED;
    });
  }
}
