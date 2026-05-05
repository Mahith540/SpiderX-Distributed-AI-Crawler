package com.spiderx.frontier.impl;

import com.spiderx.frontier.FrontierService;
import com.spiderx.model.CrawlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedFrontierService implements FrontierService {

    private final KafkaTemplate<String, CrawlRequest> kafkaTemplate;
    private final StringRedisTemplate redisTemplate;

    private static final String VISITED_SET_KEY = "spiderx:visited_urls";
    private static final String TOPIC_HIGH_PRIORITY = "spiderx.crawl.high";
    private static final String TOPIC_DEFAULT_PRIORITY = "spiderx.crawl.default";

    @Override
    public void enqueue(CrawlRequest request) {
        if (isVisited(request.getUrl())) {
            return;
        }

        String topic = request.getPriorityScore() > 0.7 ? TOPIC_HIGH_PRIORITY : TOPIC_DEFAULT_PRIORITY;
        
        // Advanced: Use domain as partition key to ensure politeness per domain
        String domain = getDomain(request.getUrl());
        
        kafkaTemplate.send(topic, domain, request)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Enqueued URL: {} to topic: {}", request.getUrl(), topic);
                    } else {
                        log.error("Failed to enqueue URL: {}", request.getUrl(), ex);
                    }
                });
    }

    @Override
    public boolean isVisited(String url) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(VISITED_SET_KEY, url));
    }

    @Override
    public void markVisited(String url) {
        redisTemplate.opsForSet().add(VISITED_SET_KEY, url);
    }

    private String getDomain(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
