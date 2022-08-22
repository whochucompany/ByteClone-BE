package com.whochucompany.byteclone.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.whochucompany.byteclone.controller.ResponseDto;
import com.whochucompany.byteclone.domain.news.enums.NewsType;
import com.whochucompany.byteclone.domain.news.News;
import com.whochucompany.byteclone.domain.news.enums.ViewAuthority;
import com.whochucompany.byteclone.domain.news.dto.NewsRequestDto;
import com.whochucompany.byteclone.domain.repository.NewsRepository;
import com.whochucompany.byteclone.util.ImageUrlProccessingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")  // "junior-test-bucket"
    private String bucketName;

    private final String cloudFrontDomain = "https://d1ig9s8koiuspp.cloudfront.net/";


    // 뉴스 작성
    @Transactional
    public ResponseDto<?> createNews(NewsRequestDto requestDto, HttpServletRequest request) throws IOException {
        // 회원 로그인 확인 로직

        ViewAuthority view = null;
        NewsType newsType = null;

        // 읽기 권한을 구분하는 로직
        try {
            view = ViewAuthority.valueOf(requestDto.getView());
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail("BAD_REQUEST", "VIEW에 없는 항목입니다.");
        }

        // 기사 주제를 구분하는 로직
        try {
            newsType = NewsType.valueOf(requestDto.getNewsType());
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail("BAD_REQUEST", "CATEGORY에 없는 항목입니다.");
        }

        String result = null;

        // 이미지 파일이 비어있지 않은 경우, 작동하는 로직
        if (!requestDto.getImage().isEmpty()) {
            String fileName = ImageUrlProccessingUtils.buildFileName(requestDto.getImage().getOriginalFilename());

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(requestDto.getImage().getContentType());

            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, requestDto.getImage().getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            // S3 업로드 후, 저장된 url을 result에 넣어줌
            result = amazonS3Client.getUrl(bucketName, fileName).toString();

            // CloudFront 도메인을 통해 바로 이미지를 보여주기 위한 처리 로직
            String[] nameWithNoS3info = result.split(".com/");
            String proccessedFileName = nameWithNoS3info[1];

            // 브라우저에 result를 넣으면, 이미지를 바로 볼 수 있음
            result = cloudFrontDomain + proccessedFileName;
        }

        // ** 추가 필요, Member는 회원 검증 로직에서 가져옴


        News news = News.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent()) // 얘는 크기 크니깐 return 주지 말까?
                .image(result)
                .view(view)
                .newsType(newsType)
                .build();
        newsRepository.save(news);

        return ResponseDto.success(news);
    }

    // 업데이트
    @Transactional
    public ResponseDto<?> updateNews(Long newsId, NewsRequestDto requestDto, HttpServletRequest request) throws IOException {

        // 회원 토큰 검증 로직

        // 글 작성자 검증 로직

        // news 게시글 존재 유무 확인 로직
//        News news = newsRepository.findByNewsId(newsId); // 왜 redundant?
        Optional<News> optionalNews = Optional.ofNullable(newsRepository.findByNewsId(newsId));
        News news = optionalNews.orElse(null);
        if (null == news) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 news ID 입니다.");
        }

        // 회원 정보 가져와서 작성자 검증

        // 읽기 권한 카테고리 수정 로직
        ViewAuthority view = null;

        try {
            view = ViewAuthority.valueOf(requestDto.getView());

        } catch (IllegalArgumentException e) {
            return ResponseDto.fail("BAD_REQUEST", "VIEW에 없는 항목입니다.");
        }

        NewsType newsType = null;

        try {
            newsType = NewsType.valueOf(requestDto.getNewsType());
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail("BAD_REQUEST", "CATEGORY에 없는 항목입니다.");
        }

        String result = null;

        if (!requestDto.getImage().isEmpty()) {
            String fileName = ImageUrlProccessingUtils.buildFileName(requestDto.getImage().getOriginalFilename());

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(requestDto.getImage().getContentType());

            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, requestDto.getImage().getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            result = amazonS3Client.getUrl(bucketName, fileName).toString();

            String[] nameWithNoS3info = result.split(".com/");
            String proccessedFileName = nameWithNoS3info[1];

            result = cloudFrontDomain + proccessedFileName;
        }


        news.updateNews(requestDto, result);
        return ResponseDto.success(news);

    }

    // 조회: 전체 조회 + 상세 조회 and 전체조회는 비회원, 카테고리별 로 구성됨
    @Transactional(readOnly = true)
    public Page<News> readAllNewsList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return newsRepository.findAllByOrderByCreatedAt(pageable);
    }

    // newsType 별 전체 조회
    @Transactional
    public Page<News> readAllNewsTypeList(int page, int size, String newsType, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;  // 3항 연산자, t/f 값을 반환
        Sort newstype = Sort.by(direction, newsType);
        Pageable pageable = PageRequest.of(page, size, newstype);

        return newsRepository.findAllByNewsType(newsType, pageable);
    }

//    @Transactional(readOnly = true)
//    public Page<News> readAllNewsList(int page, int size, boolean isAsc) {
//        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;  // 3항연산자, t/f 값을 반환
//        Sort sort = Sort.by(direction, sortBy);  // direction
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        return newsRepository.findAllByNewsType(newsType, pageable);
//    }

    // 상세 조회

}
