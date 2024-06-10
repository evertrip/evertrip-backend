package com.evertrip.post.reader;

import com.evertrip.post.service.RedisForCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewUpdateReader implements ItemReader<Map.Entry<Long,Long>> {

    private final RedisForCacheService redisForCacheService;

    private Iterator<Map.Entry<Long, Long>> iterator;



    @Override
    public Map.Entry<Long, Long> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("ViewUpdateReader 호출");

        if (iterator == null) {
            HashMap<Long, Long> data = redisForCacheService.getViewMapForUpdate();
            iterator = data.entrySet().iterator();
        }

        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }
}
