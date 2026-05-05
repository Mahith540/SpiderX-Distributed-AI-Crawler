package com.spiderx.indexer;

import com.spiderx.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
@Service
public class LuceneIndexer {

    private IndexWriter indexWriter;
    
    @Value("${spiderx.index.path:./spiderx-index}")
    private String indexPath;

    @PostConstruct
    public void init() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        // Optimize for write performance
        config.setRAMBufferSizeMB(256.0);
        this.indexWriter = new IndexWriter(dir, config);
        log.info("Initialized Lucene Indexer at: {}", indexPath);
    }

    @KafkaListener(topics = "spiderx.index.tasks", groupId = "spiderx-indexer-group")
    public void onPageIndexTask(PageData data) {
        indexPage(data);
    }

    public void indexPage(PageData data) {
        try {
            Document doc = new Document();
            doc.add(new StringField("url", data.getUrl(), Field.Store.YES));
            doc.add(new TextField("title", data.getTitle(), Field.Store.YES));
            doc.add(new TextField("content", data.getContent(), Field.Store.YES));
            doc.add(new StringField("crawledAt", data.getCrawledAt().toString(), Field.Store.YES));
            
            // Add relevance score for boosting results later
            doc.add(new TextField("relevance", String.valueOf(data.getRelevanceScore()), Field.Store.YES));

            // Update document (overwrites if URL already exists)
            indexWriter.updateDocument(new org.apache.lucene.index.Term("url", data.getUrl()), doc);
            indexWriter.commit();
            log.debug("Indexed page: {}", data.getUrl());
        } catch (IOException e) {
            log.error("Failed to index page {}: {}", data.getUrl(), e.getMessage());
        }
    }

    @PreDestroy
    public void close() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }
}
