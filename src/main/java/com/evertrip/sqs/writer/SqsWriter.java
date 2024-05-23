package com.evertrip.sqs.writer;

import com.evertrip.post.entity.PostLog;
import com.evertrip.post.repository.PostLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsWriter implements ItemWriter<PostLog> {

    private final PostLogRepository postLogRepository;

    @Override
    public void write(Chunk<? extends PostLog> chunk) throws Exception {
        List<? extends PostLog> items = chunk.getItems();
        postLogRepository.saveAll(items);
        log.info("SqsWriter 호출 :" + items.size());
    }
}
