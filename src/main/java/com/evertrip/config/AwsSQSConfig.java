package com.evertrip.config;

import com.evertrip.sqs.converter.TextPlainJsonMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementOrdering;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class AwsSQSConfig {

    @Value("${cloud.aws.credentials.accessKey}")
    private String AWS_ACCESS_KEY;

    @Value("${cloud.aws.credentials.secretKey}")
    private String AWS_SECRET_KEY;

    @Value("${cloud.aws.region.static}")
    private String AWS_REGION;

    private ObjectMapper objectMapper;



    // 클라이언트 설정: region과 자격증명
    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return AWS_ACCESS_KEY;
                    }

                    @Override
                    public String secretAccessKey() {
                        return AWS_SECRET_KEY;
                    }
                })
                .region(Region.of(AWS_REGION))
                .build();
    }

    // Listener Factory 설정 (Listener 쪽)
    @Bean
    SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
        return SqsMessageListenerContainerFactory
                .builder()
                .configure(options -> options
                        .acknowledgementMode(AcknowledgementMode.ALWAYS)
                        .acknowledgementInterval(Duration.ofSeconds(3))
                        .acknowledgementThreshold(5)
                        .acknowledgementOrdering(AcknowledgementOrdering.ORDERED)
                        .messageConverter(sqsMessagingMessageConverter(objectMapper))
                )
                .sqsAsyncClient(sqsAsyncClient)
                .build();
    }

    // 메시지 발송을 위한 SQS 템플릿 설정 (Sender 쪽)
    @Bean
    public SqsTemplate sqsTemplate() {
        return SqsTemplate.newTemplate(sqsAsyncClient());
    }

    @Bean
    public SqsMessagingMessageConverter sqsMessagingMessageConverter(ObjectMapper objectMapper) {
        SqsMessagingMessageConverter converter = new SqsMessagingMessageConverter();
        TextPlainJsonMessageConverter payloadConverter = new TextPlainJsonMessageConverter(objectMapper);
        converter.setPayloadMessageConverter(payloadConverter);
        return converter;
    }

}
