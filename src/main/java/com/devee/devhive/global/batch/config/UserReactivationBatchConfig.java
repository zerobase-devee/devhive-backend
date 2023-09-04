package com.devee.devhive.global.batch.config;

import com.devee.devhive.domain.user.exithistory.service.ExitHistoryService;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.domain.user.type.ActivityStatus;
import java.util.List;
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
public class UserReactivationBatchConfig {

  private final ExitHistoryService exitHistoryService;
  private final UserService userService;

  @Bean(name = "userReactivationJob")
  public Job userReactivationJob(JobRepository jobRepository, @Qualifier("userReactivationStep") Step step) {
    return new JobBuilder("userReactivationJob", jobRepository)
        .start(step)
        .build();
  }

  @Bean(name = "userReactivationStep")
  public Step userReactivationStep(JobRepository jobRepository, Tasklet userReactivationTasklet,
      PlatformTransactionManager platformTransactionManager) {
    return new StepBuilder("userReactivationStep", jobRepository)
        .tasklet(userReactivationTasklet, platformTransactionManager)
        .build();
  }

  @Bean(name = "userReactivationTasklet")
  public Tasklet userReactivationTasklet() {
    return ((contribution, chunkContext) -> {
      log.info("정지 기한이 지난 유저 재활성화.");

      List<Long> reactivatingUsers = exitHistoryService.getReactivatingUsers();

      reactivatingUsers.forEach(userId ->
          userService.setUserStatus(userService.getUserById(userId), ActivityStatus.ACTIVITY));

      return RepeatStatus.FINISHED;
    });
  }
}
