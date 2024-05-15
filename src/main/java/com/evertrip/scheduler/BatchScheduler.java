package com.evertrip.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job sqsBatchJob;

    @Scheduled(cron = "0 */30 * * * ?")  // 매 30분마다 실행
    public void runBatchJob() throws JobExecutionException {
        jobLauncher.run(sqsBatchJob, new JobParametersBuilder()
                .addLong("uniqueness", System.nanoTime()).toJobParameters());
    }
}
