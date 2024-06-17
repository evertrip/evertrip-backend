package com.evertrip.config;

import com.evertrip.post.dto.sqs.PostLogDto;
import com.evertrip.post.entity.PostLog;
import com.evertrip.post.reader.ViewUpdateReader;
import com.evertrip.post.service.RedisForCacheService;
import com.evertrip.post.writer.ViewUpdateWriter;
import com.evertrip.sqs.processor.SqsProcessor;
import com.evertrip.sqs.writer.SqsWriter;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
//@EnableConfigurationProperties(BatchProperties.class)
@Slf4j
public class BatchConfig {

    private JobRepository jobRepository;

    private PlatformTransactionManager platformTransactionManager;

    private SqsWriter sqsWriter;
    private SqsProcessor sqsProcessor;

    private ViewUpdateReader viewUpdateReader;

    private ViewUpdateWriter viewUpdateWriter;


    private String queueName;



    private SqsTemplate sqsTemplate;


    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager platformTransactionManager,
                       SqsWriter sqsWriter,
                       SqsProcessor sqsProcessor,
                       SqsTemplate sqsTemplate,
                       ViewUpdateReader viewUpdateReader,
                       ViewUpdateWriter viewUpdateWriter,
                       @Value("${cloud.aws.sqs.queue-name}") String queueName) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.sqsWriter = sqsWriter;
        this.sqsProcessor = sqsProcessor;
        this.sqsTemplate = sqsTemplate;
        this.queueName = queueName;
        this.viewUpdateReader = viewUpdateReader;
        this.viewUpdateWriter = viewUpdateWriter;
    }


    @Bean
    public Job sqsBatchJob(Step sqsStep) {
        return new JobBuilder("sqsBatchJob", this.jobRepository)
                .start(sqsStep)
                .build();
    }

    @Bean
    public Job viewUpdateJob(Step viewUpdateStep) {
        return new JobBuilder("viewUpdateJob", this.jobRepository)
                .start(viewUpdateStep)
                .build();
    }

    @Bean
    public Step sqsStep() {
        return new StepBuilder("sqsBatchStep", this.jobRepository)
                .<PostLogDto, PostLog>chunk(10, platformTransactionManager)
                .reader(sqsReader())
                .processor(sqsProcessor)
                .writer(sqsWriter)
                .build();
    }

    @Bean
    public Step viewUpdateStep() {
        return new StepBuilder("viewUpdateStep", this.jobRepository)
                .<Map.Entry<Long, Long>, Map.Entry<Long, Long>>chunk(10, platformTransactionManager)
                .reader(viewUpdateReader)
                .writer(viewUpdateWriter)
                .build();

    }



    // 최신꺼
    @Bean
    @StepScope
    public ItemReader<PostLogDto> sqsReader() {
        log.info("SQS 메시지 읽기 작업 호출");
        List<PostLogDto> inputDataList = new ArrayList<>();
        boolean continuePolling = true;

        while (continuePolling) {
            System.out.println("폴링 작업 호출 : 메시지 수신 중");
            try {
                // SQS 폴링 작업
                Collection<Message<PostLogDto>> messages = sqsTemplate.receiveMany(from -> from.queue(queueName)
                        .maxNumberOfMessages(10)
                        .pollTimeout(Duration.ofSeconds(10)), PostLogDto.class);

                if (messages == null || messages.isEmpty()) {
                    continuePolling = false;  // 메시지가 없으면 반복 종료
                } else {
                        List<PostLogDto> extractedData = messages.stream()
                                .map(Message::getPayload)
                                .collect(Collectors.toList());
                        inputDataList.addAll(extractedData);

                        log.info("SQS 읽은 데이터 목록 :");
                        for (PostLogDto inputData : inputDataList) {
                            log.info("{}", inputData);
                        }
                    }
                } catch (Exception e) {
                log.error("Error receiving messages from SQS", e);
                continuePolling = false;
            }
        }


        return new ListItemReader<>(inputDataList);
    }



}
