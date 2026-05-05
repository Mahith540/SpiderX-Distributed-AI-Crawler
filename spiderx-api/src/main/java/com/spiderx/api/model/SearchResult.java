package com.spiderx.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResult {
    private String url;
    private String title;
    private String snippet;
    private float score;
}
