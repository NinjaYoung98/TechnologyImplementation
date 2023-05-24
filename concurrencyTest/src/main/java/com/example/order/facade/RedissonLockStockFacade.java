package com.example.order.facade;

import com.example.order.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockStockFacade {
    private final RedissonClient redissonClient;
    private final StockService stockService;

    public void decrease(Long key, Long quantity) {
        RLock lock = redissonClient.getLock(key.toString());//Lock 객체 가져옴
        try {
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
            if (!available) {
                log.info("lock 획득 실패");
                return;
            }
            stockService.decrease(key, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
