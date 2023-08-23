package com.devee.devhive.domain.project.vote.service;


import com.devee.devhive.global.config.BatchConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteProcessingScheduler {

  private final JobLauncher jobLauncher;
  private final BatchConfig batchConfig;
  private final JobRepository jobRepository;
  private final Step step;

  @Scheduled(cron = "0/5 * * * * *")
  public void processVote() {
    log.info("5초에 한 번씩 실행");
    Map<String, JobParameter<?>> confMap = new HashMap<>();
    confMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
    JobParameters jobParameters = new JobParameters(confMap);

    try {
      jobLauncher.run(batchConfig.voteProcessJob(jobRepository, step), jobParameters);
    } catch (Exception e) {
      log.info("error: {}", e.getMessage());
    }
  }
}
