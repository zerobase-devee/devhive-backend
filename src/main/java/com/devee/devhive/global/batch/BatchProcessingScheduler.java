package com.devee.devhive.global.batch;


import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchProcessingScheduler {

  private final JobLauncher jobLauncher;
  private final JobRegistry jobRegistry;

  @Bean
  public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
    jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
    return jobRegistryBeanPostProcessor;
  }

  // 매 0분마다 처리
  @Scheduled(cron = "* 0 * * * *")
  public void processVote() {
    JobParameters jobParameters = getJobParameters();

    try {
      jobLauncher.run(jobRegistry.getJob("voteProcessJob"), jobParameters);
    } catch (Exception e) {
      log.info("error: {}", e.getMessage());
    }
  }

  @Scheduled(cron = "* 0 * * * *")
  public void userReactivation() {
    JobParameters jobParameters = getJobParameters();

    try {
      jobLauncher.run(jobRegistry.getJob("userReactivationJob"), jobParameters);
    } catch (Exception e) {
      log.info("error: {}", e.getMessage());
    }
  }

  @Scheduled(cron = "* 0 * * * *")
  public void projectRecruitProcess() {
    JobParameters jobParameters = getJobParameters();

    try {
      jobLauncher.run(jobRegistry.getJob("recruitProcessJob"), jobParameters);
    } catch (Exception e) {
      log.info("error: {}", e.getMessage());
    }
  }

  private JobParameters getJobParameters() {
    Map<String, JobParameter<?>> confMap = new HashMap<>();
    confMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
    return new JobParameters(confMap);
  }
}
