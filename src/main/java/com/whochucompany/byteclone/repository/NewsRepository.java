package com.whochucompany.byteclone.domain.repository;

import com.whochucompany.byteclone.domain.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByOrderByCreatedAtDesc();

    // 뉴스기사 아이디로 검색
    News findByNewsId(Long newsId);

    // 페이지네이션 관련
    Page<News> findAllByOrderByCreatedAt(Pageable pageable);

    // newsType 별 페이지네이션
    Page<News> findAllByNewsType(String newsType, Pageable pageable);
}
