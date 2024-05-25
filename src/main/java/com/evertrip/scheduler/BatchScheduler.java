package com.evertrip.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class BatchScheduler {


    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job sqsBatchJob;

    @Autowired
    private Job viewUpdateJob;

//    @Scheduled(cron = "0 */15 * * * ?")  // 매 15분마다 실행
//    public void runSqsUpdateJob() throws JobExecutionException {
//        log.info("SQS 스케줄링 동작");
//        jobLauncher.run(sqsBatchJob, new JobParametersBuilder()
//                .addLong("uniqueness", System.nanoTime()).toJobParameters());
//    }

    @Scheduled(cron = "0 */15 * * * ?") // 매 15분마다 실행
    public void runViewUpdateJob() throws JobExecutionException {
        log.info("View Update 스케줄링 동작");
        jobLauncher.run(viewUpdateJob, new JobParametersBuilder().addLong("viewUpdateTimes", System.nanoTime()).toJobParameters());
    }
}
