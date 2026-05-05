package com.spiderx.frontier;

import com.spiderx.model.CrawlRequest;

public interface FrontierService {
    /**
     * Enqueues a URL for crawling.
     * @param request The crawl request containing URL and priority.
     */
    void enqueue(CrawlRequest request);

    /**
     * Checks if a URL has already been visited.
     * @param url The URL to check.
     * @return true if visited, false otherwise.
     */
    boolean isVisited(String url);

    /**
     * Marks a URL as visited.
     * @param url The URL to mark.
     */
    void markVisited(String url);
}
