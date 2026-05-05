package com.spiderx.api.controller;

import com.spiderx.api.model.SearchResult;
import com.spiderx.api.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public List<SearchResult> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {
        return searchService.search(q, limit);
    }
}
