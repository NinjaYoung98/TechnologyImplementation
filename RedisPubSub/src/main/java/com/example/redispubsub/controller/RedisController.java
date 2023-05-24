package com.example.redispubsub.controller;

import com.example.redispubsub.configuration.RedisMessagePublisher;
import com.example.redispubsub.configuration.RedisMessageSubscriber;
import com.example.redispubsub.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/redis")
@Slf4j
@RequiredArgsConstructor
public class RedisController {
    private final RedisMessagePublisher redisMessagePublisher;

    @PostMapping("/publish")
    public void publish(@RequestBody Message message){
        log.info(">> publishing : {}" ,message);
        redisMessagePublisher.publish(message.toString());
    }
    @GetMapping("/subscribe")
    public List<String> getMessages(){
        return RedisMessageSubscriber.messageList;
    }
}
