package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentRequest {
    private String title;
    private String description;
    private String category;
    private String creatorId;
    private String mediaType;
    private String mediaUrl;
    private List<String> tags;
}
