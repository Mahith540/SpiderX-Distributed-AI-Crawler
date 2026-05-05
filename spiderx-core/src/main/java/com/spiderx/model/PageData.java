package com.spiderx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageData implements Serializable {
    private String url;
    private String title;
    private String content;
    private List<String> outlinks;
    private LocalDateTime crawledAt;
    private double relevanceScore;
}
