package com.evertrip.post.service;

import com.evertrip.constant.ConstantPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@DirtiesContext
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private RedisForCacheService redisForCacheService;

    @Autowired
    private PostCacheService postCacheService;

    @Autowired
    private CacheManager cacheManager;

    private Long postId;

    @BeforeEach
    public void setUp() {
        postId = 1L;

        // 초기 조회수 설정
        Cache viewsCache = cacheManager.getCache(ConstantPool.CacheName.VIEWS);
        if (viewsCache != null) {
            viewsCache.put(postId.toString(), 0L);
        }

        // 초기 방문자 리스트 비우기
        redisForCacheService.deleteSet(ConstantPool.CacheName.VIEWERS + ":" + postId);
    }

    @AfterEach
    public void tearDown() {
        postId = 1L;

        // 조회수 삭제
        Cache viewsCache = cacheManager.getCache(ConstantPool.CacheName.VIEWS);
        viewsCache.evict(postId.toString());


        // 방문자 리스트 비우기
        redisForCacheService.deleteSet(ConstantPool.CacheName.VIEWERS + ":" + postId);
    }

    @DisplayName("분산락 적용 조회수 증가 테스트")
    @Test
    public void testConcurrentViewCountIncrement() throws InterruptedException, ExecutionException, TimeoutException {
        int numberOfThreads = 100; // 동시 요청 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 여러 스레드에서 동시에 요청 실행
        Future<Void>[] futures = new Future[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            int memberId = i + 1;
            Callable<Void> task = () -> {
                postService.getPostDetailV2(postId, (long) memberId);
                return null;
            };
            futures[i] = executorService.submit(task);
        }

        // 결과 대기 및 확인
        for (Future<Void> future : futures) {
            future.get(5, TimeUnit.SECONDS);
        }

        executorService.shutdown();

        // 최종 조회수 확인
        Long finalViews = postCacheService.getViews(postId).longValue();
        assertEquals(numberOfThreads, finalViews); // 모든 요청이 들어와서 조회수가 정확히 증가했는지 확인
    }


}
