package com.whochucompany.byteclone.controller;

import com.whochucompany.byteclone.domain.news.News;
import com.whochucompany.byteclone.domain.news.dto.NewsRequestDto;
import com.whochucompany.byteclone.domain.news.dto.NewsResponseDto;
import com.whochucompany.byteclone.domain.news.dto.PageDto;
import com.whochucompany.byteclone.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/news")
public class NewsController {

    private final NewsService newsService;

    // 뉴스기사 작성
    @PostMapping
    public ResponseDto<?> createNews(@ModelAttribute NewsRequestDto requestDto, HttpServletRequest request) throws IOException {
        return newsService.createNews(requestDto, request);
    }

    // 뉴스기사 수정
    @PutMapping(value = "/{newsId}")
    public ResponseDto<?> updateNews(@PathVariable Long newsId, @ModelAttribute NewsRequestDto requestDto, HttpServletRequest request) throws IOException {
        return  newsService.updateNews(newsId, requestDto, request);
    }

    // 뉴스기사 전체 조회 == 메인 페이지
    @GetMapping
    public PageDto readAllNewsList(@RequestParam("page") int page)
    {
        page = page -1;  // client에서 1로 들어오면 서버에서 1을 빼서 0부터 인식하도록
        int size = 12;  // page 당 12 개
        return newsService.readAllNewsList(page, size);
    }

    // newsType 별 전체 조회
    @GetMapping(value = "/{newsType}")
    public NewsResponseDto readAllNewsTypeList(@RequestParam("page") int page,
                                          @RequestParam("size") int size,
                                          @RequestParam("newsType") String newsType,
                                          @RequestParam("isAsc") boolean isAsc)
    {
        page = page -1; // client에서 1로 들어오면 서버에서 1을 빼서 0부터 인식하도록

        return newsService.readAllNewsTypeList(page, size, newsType, isAsc);
    }

}
