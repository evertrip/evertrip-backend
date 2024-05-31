package com.evertrip.scheduler;

import com.evertrip.constant.ConstantPool;
import com.evertrip.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class PostUpdateScheduler {

    private final PostService postService;

    //10분에 1번씩 best, view
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void updateBestAndViewPost(){
        try {
            log.info("update for best and view posts");
            Pageable pageable = PageRequest.of(0, ConstantPool.MAIN_POST_SIZE_FOR_SLIDE);
            postService.getPostBest30(pageable);
            postService.getPostView30(pageable);
            log.info("Successfully updated best and view posts.");
        } catch (Exception e) {
            log.error("Error during scheduled update of best and view posts", e);
        }

    }
}


