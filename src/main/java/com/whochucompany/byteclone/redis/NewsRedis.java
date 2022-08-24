package com.whochucompany.byteclone.redis;

import com.whochucompany.byteclone.domain.comment.Comment;
import com.whochucompany.byteclone.domain.member.Member;
import com.whochucompany.byteclone.domain.news.dto.NewsRequestDto;
import com.whochucompany.byteclone.domain.news.enums.Category;
import com.whochucompany.byteclone.domain.news.enums.View;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@RedisHash("News")
public class NewsRedis {


    @Id
    private String newsId;
    private String title;
    private String content;
    private String image;
    private View view;  // 뉴스 기사 읽기 권한
    private Category category;  // 뉴스 기사 카테고리
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Member member;
    private List<Comment> commentList;



    public void updateNews(NewsRequestDto newsRequestDto, String image) {
        this.title = newsRequestDto.getTitle();
        this.content = newsRequestDto.getContent();
        this.image = image;
        this.view = View.valueOf(newsRequestDto.getView());
        this.category = Category.valueOf(newsRequestDto.getCategory());
    }


}