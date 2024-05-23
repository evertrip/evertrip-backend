package com.evertrip.sqs.converter;

import com.evertrip.post.dto.sqs.PostLogDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class TextPlainJsonMessageConverter extends AbstractMessageConverter {

    private ObjectMapper objectMapper;

    public TextPlainJsonMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        if (message.getPayload() instanceof String) {
            String payload = (String) message.getPayload();
            try {
                return objectMapper.readValue(payload, PostLogDto.class);
            } catch (IOException e) {
                log.error("Unable to convert message", e);
                return null;
            }
        }
        return null;
    }
}
