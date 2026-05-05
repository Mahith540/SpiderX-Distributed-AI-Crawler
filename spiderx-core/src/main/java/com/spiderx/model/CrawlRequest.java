package com.spiderx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlRequest implements Serializable {
    private String url;
    private int depth;
    private double priorityScore;
    private String sourceUrl;
    private String anchorText;
    private Map<String, String> metadata;
}
