package com.example.order.facade;

import com.example.order.domain.entity.Stock;
import com.example.order.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLockStockFacadeTest {
    @Autowired
    private RedisLockStockFacade redisLockStockFacade;
    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100; //100명의 동시 요청이기 때문
        ExecutorService executorService = Executors.newFixedThreadPool(32);    //멀티 스레드 이용하기 위해
        // 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 돕는 자바 api
        CountDownLatch latch = new CountDownLatch(threadCount); //다른 스레드에서 수행중인 작업이 완료될때까지 대기

        for (int i = 0; i <= threadCount; i++) {
            executorService.submit(() -> {
                try {
                    redisLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - (1*100) = 0
        assertEquals(0, stock.getQuantity());
    }

}