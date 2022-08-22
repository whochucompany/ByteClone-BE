package com.whochucompany.byteclone.domain.news.dto;

import com.whochucompany.byteclone.domain.news.enums.ViewAuthority;

import java.time.LocalDateTime;

public class NewsResponseDto {

    private String title;
    private String image;
    private String content;
    private ViewAuthority view;
    private LocalDateTime createdAt;
}
