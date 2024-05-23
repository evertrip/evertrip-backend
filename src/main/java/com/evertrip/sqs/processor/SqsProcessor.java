package com.evertrip.sqs.processor;

import com.evertrip.post.dto.sqs.PostLogDto;
import com.evertrip.post.entity.PostLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SqsProcessor implements ItemProcessor<PostLogDto, PostLog> {

    @Override
    public PostLog process(PostLogDto dto) throws Exception {
        PostLog postLogEntity = new PostLog(dto);
        log.info("SqsProcessor 호출 :" + dto);
        return postLogEntity;
    }
}
