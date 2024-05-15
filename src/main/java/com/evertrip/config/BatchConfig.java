package com.evertrip.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;



    @Bean
    public Job sqsBatchJob() {
        return new JobBuilder("sqsBatchJob", this.jobRepository)
                .start(sqsStep())
                .build();
    }

    @Bean
    public Step sqsStep() {
        return new StepBuilder("sqsBatchStep", this.jobRepository)
                .<GpsRequestDto, Gps>chunk(10, platformTransactionManager)
                .reader(itemReader(null))
                .processor(gpsProcessor)
                .writer(gpsWriter)
                .listener(GpsStepListener)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public ItemReader<Message> sqsReader() {
        // SQS 폴링 로직을 여기에 구현합니다.
        return new SqsMessageReader(amazonSQS, "your-queue-url");
    }

    @Bean
    public ItemWriter<Message> sqsWriter() {
        return messages -> {
            // 메시지를 데이터베이스에 배치로 쓰는 로직을 구현합니다.
            for (Message message : messages) {
                System.out.println("Processing message: " + message.getBody());
                // 데이터베이스에 저장하는 로직 추가
            }
        };
    }
}
