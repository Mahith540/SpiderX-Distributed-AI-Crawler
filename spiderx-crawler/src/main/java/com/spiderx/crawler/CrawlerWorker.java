package com.spiderx.crawler;

import com.spiderx.frontier.FrontierService;
import com.spiderx.model.CrawlRequest;
import com.spiderx.model.PageData;
import com.spiderx.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerWorker {

    private final FrontierService frontierService;
    private final ScoringService scoringService;
    private final KafkaTemplate<String, PageData> kafkaTemplate;
    
    // Using Virtual Threads for high-concurrency fetching
    private final ExecutorService fetcherExecutor = Executors.newVirtualThreadPerTaskExecutor();

    private static final String INDEX_TOPIC = "spiderx.index.tasks";
    private static final String TARGET_CATEGORY = "technology"; // Default category for now

    @KafkaListener(topics = {"spiderx.crawl.high", "spiderx.crawl.default"}, groupId = "spiderx-crawler-group")
    public void onCrawlRequest(CrawlRequest request) {
        fetcherExecutor.submit(() -> crawl(request));
    }

    private void crawl(CrawlRequest request) {
        if (frontierService.isVisited(request.getUrl())) {
            return;
        }

        log.info("Crawling: {}", request.getUrl());
        try {
            // 1. Fetch and Parse
            Document doc = Jsoup.connect(request.getUrl())
                    .timeout(10000)
                    .userAgent("SpiderX/1.0 (+http://spiderx.com)")
                    .get();

            String title = doc.title();
            String text = doc.body().text();
            
            // 2. Extract and Score Outlinks
            Elements links = doc.select("a[href]");
            List<String> outlinks = new ArrayList<>();

            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                String anchorText = link.text();

                if (isValid(absUrl)) {
                    // Smart Scoring: Prioritize this link based on anchor text
                    double priority = scoringService.calculateScore(anchorText, TARGET_CATEGORY);
                    
                    CrawlRequest nextRequest = CrawlRequest.builder()
                            .url(absUrl)
                            .depth(request.getDepth() + 1)
                            .priorityScore(priority)
                            .sourceUrl(request.getUrl())
                            .anchorText(anchorText)
                            .build();

                    frontierService.enqueue(nextRequest);
                    outlinks.add(absUrl);
                }
            }

            // 3. Mark as Visited
            frontierService.markVisited(request.getUrl());

            // 4. Send to Indexer
            PageData pageData = PageData.builder()
                    .url(request.getUrl())
                    .title(title)
                    .content(text)
                    .outlinks(outlinks)
                    .crawledAt(LocalDateTime.now())
                    .relevanceScore(scoringService.calculateScore(text, TARGET_CATEGORY))
                    .build();

            kafkaTemplate.send(INDEX_TOPIC, request.getUrl(), pageData);

        } catch (Exception e) {
            log.error("Error crawling {}: {}", request.getUrl(), e.getMessage());
        }
    }

    private boolean isValid(String url) {
        return url != null && url.startsWith("http") && !url.contains("#");
    }
}
