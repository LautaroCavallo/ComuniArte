package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentResponse {
    private String id;
    private String title;
    private String description;
    private String category;
    private String mediaType;
    private String mediaUrl;
    private List<String> tags;
    private LocalDateTime createdAt;
    private Long totalViews;
    private UserSummary creator;
}
