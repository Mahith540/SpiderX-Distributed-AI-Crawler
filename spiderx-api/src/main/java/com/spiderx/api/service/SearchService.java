package com.spiderx.api.service;

import com.spiderx.api.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchService {

    @Value("${spiderx.index.path:./spiderx-index}")
    private String indexPath;

    public List<SearchResult> search(String queryString, int limit) {
        List<SearchResult> results = new ArrayList<>();
        try (Directory dir = FSDirectory.open(Paths.get(indexPath));
             IndexReader reader = DirectoryReader.open(dir)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query query = parser.parse(queryString);

            TopDocs topDocs = searcher.search(query, limit);
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                results.add(SearchResult.builder()
                        .url(doc.get("url"))
                        .title(doc.get("title"))
                        .score(sd.score)
                        .snippet(extractSnippet(doc.get("content"), queryString))
                        .build());
            }
        } catch (Exception e) {
            log.error("Search failed for query '{}': {}", queryString, e.getMessage());
        }
        return results;
    }

    private String extractSnippet(String content, String query) {
        if (content == null) return "";
        int index = content.toLowerCase().indexOf(query.toLowerCase());
        if (index == -1) return content.substring(0, Math.min(content.length(), 200)) + "...";
        
        int start = Math.max(0, index - 50);
        int end = Math.min(content.length(), index + 150);
        return (start > 0 ? "..." : "") + content.substring(start, end).trim() + (end < content.length() ? "..." : "");
    }
}
