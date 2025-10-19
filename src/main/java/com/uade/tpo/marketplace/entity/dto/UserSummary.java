package com.uade.tpo.marketplace.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummary{
    private String id;
    private String name;
    private String profilePictureUrl;
}
