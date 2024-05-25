package com.evertrip.post.writer;

import com.evertrip.post.entity.PostLog;
import com.evertrip.post.repository.PostLogRepository;
import com.evertrip.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class ViewUpdateWriter implements ItemWriter<Map.Entry<Long,Long>> {

    private final PostRepository postRepository;




    @Override
    public void write(Chunk<? extends Map.Entry<Long,Long>> chunk) throws Exception {
        List<? extends Map.Entry<Long, Long>> items = chunk.getItems();
        log.info("ViewUpdateWriter 호출 :" + items.size());

        for (Map.Entry<Long, Long> entry : items) {
            postRepository.updateView(entry.getKey(), entry.getValue());
        }

    }


}