package com.devee.devhive.global.batch.config;


import com.devee.devhive.domain.project.vote.service.ExitVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VoteProcessBatchConfig {

  private final ExitVoteService exitVoteService;

  @Bean(name = "voteProcessJob")
  public Job voteProcessJob(JobRepository jobRepository, @Qualifier("voteProcessStep") Step step) {
    return new JobBuilder("voteProcessJob", jobRepository)
        .start(step)
        .build();
  }

  @Bean(name = "voteProcessStep")
  public Step voteProcessStep(JobRepository jobRepository, Tasklet voteProcessTasklet,
      PlatformTransactionManager platformTransactionManager) {
    return new StepBuilder("voteProcessStep", jobRepository)
        .tasklet(voteProcessTasklet, platformTransactionManager)
        .build();
  }

  @Bean(name = "voteProcessTasklet")
  public Tasklet voteProcessTasklet() {
    return ((contribution, chunkContext) -> {
      exitVoteService.processVotes();
      return RepeatStatus.FINISHED;
    });
  }
}
