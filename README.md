# SpiderX: Distributed AI-Priority Web Crawler & Search Engine

SpiderX is a high-performance, distributed web crawling and search system designed for the modern web. It features an **AI-driven "Smart Frontier"** that prioritizes URLs based on semantic relevance, and utilizes **Java 21 Virtual Threads** for massive concurrency.

## Key Features

*   **Smart Frontier (AI-Priority)**: Uses an NLP-based scoring service (`Apache OpenNLP`) to analyze anchor text and snippets, prioritizing "high-value" links to discover relevant content 10x faster.
*   **Massive Concurrency**: Leverages **Java 21 Virtual Threads (Project Loom)** to handle thousands of concurrent I/O-bound fetching tasks with minimal memory overhead.
*   **Distributed Architecture**: Decoupled modules using **Apache Kafka** as the message backbone and **Redis** for global URL deduplication.
*   **High-Performance Search**: Full-text search capabilities powered by **Apache Lucene**, featuring ranked results and keyword-in-context snippets.
*   **Scalable Design**: Master-Worker pattern allows horizontal scaling of Fetcher and Indexer nodes.

## Tech Stack

*   **Language**: Java 21
*   **Framework**: Spring Boot 3.2
*   **Messaging**: Apache Kafka
*   **Cache/Deduplication**: Redis
*   **Search Engine**: Apache Lucene 9.10
*   **NLP**: Apache OpenNLP
*   **Parsing**: Jsoup

## System Architecture

1.  **SpiderX-Frontier**: Manages the crawl queue in Kafka. Uses Redis sets to ensure each URL is crawled only once.
2.  **SpiderX-Crawler**: Worker nodes that fetch HTML, extract outlinks, and use the AI Scorer to determine the next best URLs to visit.
3.  **SpiderX-Indexer**: Consumes crawled data and builds a sharded Lucene index.
4.  **SpiderX-API**: RESTful interface for performing real-time searches across the indexed data.



