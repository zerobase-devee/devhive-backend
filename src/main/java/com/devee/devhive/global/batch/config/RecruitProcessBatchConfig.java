package com.devee.devhive.global.batch.config;

import com.devee.devhive.domain.project.service.ProjectService;
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
public class RecruitProcessBatchConfig {

  private final ProjectService projectService;

  @Bean(name = "recruitProcessJob")
  public Job recruitProcessJob(
      JobRepository jobRepository, @Qualifier("recruitProcessStep") Step step) {
    return new JobBuilder("recruitProcessJob", jobRepository)
        .start(step)
        .build();
  }

  @Bean(name = "recruitProcessStep")
  public Step recruitProcessStep(JobRepository jobRepository, Tasklet recruitProcessTasklet,
      PlatformTransactionManager platformTransactionManager) {
    return new StepBuilder("recruitProcessStep", jobRepository)
        .tasklet(recruitProcessTasklet, platformTransactionManager)
        .build();
  }

  @Bean(name = "recruitProcessTasklet")
  public Tasklet recruitProcessTasklet() {
    return ((contribution, chunkContext) -> {
      log.info("모집 기한이 지난 프로젝트 모집 마감.");

      projectService.updateDeadlineOverProjects();

      return RepeatStatus.FINISHED;
    });
  }
}
