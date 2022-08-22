package com.whochucompany.byteclone.domain.news.dto;

import com.whochucompany.byteclone.domain.news.enums.NewsType;
import com.whochucompany.byteclone.domain.news.enums.ViewAuthority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponseDto {
    private Long id;
    private String title;
    private String image;
    private String username;
    private LocalDateTime createdAt;
    private ViewAuthority view;
    private NewsType category;
}
