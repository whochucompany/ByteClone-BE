package com.whochucompany.byteclone.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.whochucompany.byteclone.controller.ResponseDto;
import com.whochucompany.byteclone.domain.comment.Comment;
import com.whochucompany.byteclone.domain.comment.dto.CommentListDto;
import com.whochucompany.byteclone.domain.member.Member;
import com.whochucompany.byteclone.domain.news.News;
import com.whochucompany.byteclone.domain.news.dto.NewsDetailResponseDto;
import com.whochucompany.byteclone.domain.news.dto.NewsRequestDto;
import com.whochucompany.byteclone.domain.news.dto.NewsResponseDto;
import com.whochucompany.byteclone.domain.news.enums.Category;
import com.whochucompany.byteclone.domain.news.enums.View;
import com.whochucompany.byteclone.jwt.TokenProvider;
import com.whochucompany.byteclone.redis.NewsRedis;
import com.whochucompany.byteclone.redis.NewsRedisRepository;
import com.whochucompany.byteclone.redis.RedisConfig;
import com.whochucompany.byteclone.repository.CommentRepository;
import com.whochucompany.byteclone.repository.NewsRepository;
import com.whochucompany.byteclone.util.ImageUrlProccessingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.awt.print.Book;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final AmazonS3Client amazonS3Client;
    private final CommentRepository commentRepository;
    private final TokenProvider tokenProvider;
    private final NewsRedisRepository newsRedisRepository;


    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // ?????? ??????
    @Transactional
    public ResponseDto<?> createNews(NewsRequestDto requestDto, HttpServletRequest request) throws IOException {
        // ?????? ????????? ?????? ??????
        Member member = validateMember(request);

        if(null == member) {
            throw new NullPointerException("????????? ?????? ???????????????.");
        }

        View view = null;
        Category category = null;

        // ?????? ????????? ???????????? ??????
        try {
            view = View.valueOf(requestDto.getView());
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail("BAD_REQUEST", "VIEW??? ?????? ???????????????.");
        }

        // ?????? ????????? ???????????? ??????
        try {
            category = Category.valueOf(requestDto.getCategory());
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail("BAD_REQUEST", "CATEGORY??? ?????? ???????????????.");
        }

        String result = null;

        // ????????? ????????? ???????????? ?????? ??????, ???????????? ??????
        if (!requestDto.getImage().isEmpty()) {
            String fileName = ImageUrlProccessingUtils.buildFileName(requestDto.getImage().getOriginalFilename());

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(requestDto.getImage().getContentType());

            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, requestDto.getImage().getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            // S3 ????????? ???, ????????? url??? result??? ?????????
            result = amazonS3Client.getUrl(bucketName, fileName).toString();

        }

        // ** ?????? ??????, Member??? ?????? ?????? ???????????? ?????????


        News news = News.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent()) // ?????? ?????? ????????? return ?????? ???????
                .image(result)
                .view(view)
                .member(member)
                .category(category)
                .build();
        newsRepository.save(news);



        NewsResponseDto newsResponseDto =
                NewsResponseDto.builder().title(news.getTitle())
                        .image(news.getImage())
                        .content(news.getContent())
                        .view(news.getView())
                        .category(news.getCategory())
                        .createdAt(news.getCreatedAt())
                        .build();
        return ResponseDto.success(newsResponseDto);
    }

    // ????????????
    @Transactional
    public ResponseDto<?> updateNews(Long newsId, NewsRequestDto requestDto, HttpServletRequest request) throws IOException {

        String redisNewId = newsId.toString();
      newsRedisRepository.deleteById(redisNewId);


        // ?????? ?????? ?????? ??????
        Member member = validateMember(request);
        if (null == member) {
            throw new NullPointerException("????????? ?????? ???????????????.");
        }

        // news ????????? ?????? ?????? ?????? ??????
//        News news = newsRepository.findByNewsId(newsId); // ??? redundant?
        Optional<News> optionalNews = newsRepository.findByNewsId(newsId);

        News news = optionalNews.get();
        if (null == news) {
            return ResponseDto.fail("NOT_FOUND", "???????????? ?????? news ID ?????????.");
        }

        // ?????? ?????? ???????????? ????????? ??????
        if (!news.getMember().getId().equals(member.getId())) {
            return ResponseDto.fail("BAD_REQUEST", "???????????? ????????? ??? ????????????.");
        }

        // ?????? ?????? ???????????? ?????? ??????
        View view = news.getView();
        try {
            view = View.valueOf(requestDto.getView());

        } catch (IllegalArgumentException e) {
            return ResponseDto.fail("BAD_REQUEST", "VIEW??? ?????? ???????????????.");
        }

        Category category = news.getCategory();
        try {
            category = Category.valueOf(requestDto.getCategory());
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail("BAD_REQUEST", "CATEGORY??? ?????? ???????????????.");
        }

        String result = news.getImage();

        if (!requestDto.getImage().isEmpty()) {
            String fileName = ImageUrlProccessingUtils.buildFileName(requestDto.getImage().getOriginalFilename());

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(requestDto.getImage().getContentType());

            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, requestDto.getImage().getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            result = amazonS3Client.getUrl(bucketName, fileName).toString();
        }

        news.updateNews(requestDto, result);
        return ResponseDto.success(NewsResponseDto.builder()
                .newsId(news.getNewsId())
                .title(news.getTitle())
                .username(news.getMember().getUsername())
                .image(news.getImage())
                .view(news.getView())
                .category(news.getCategory())
                .createdAt(news.getCreatedAt())
                .build()
        );
    }

    // ??????: ?????? ?????? + ?????? ?????? and ??????????????? ?????????, ??????????????? ??? ?????????
    // ?????? ?????? = ?????? + ????????? ?????? ?????? ??????, (??????+??????) ?????? ????????? ?????????
    @Transactional(readOnly = true)
    public Page<NewsResponseDto> findAll(Pageable pageable) {
        Page<News> newsList = newsRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<NewsResponseDto> newsResponseDtos = convertToResponseDto(newsList);

        return newsResponseDtos;
    }

    private Page<NewsResponseDto> convertToResponseDto(Page<News> newslist) {
        List<NewsResponseDto> newsList = new ArrayList<>();

        for (News news : newslist) {
            newsList.add(
                    NewsResponseDto.builder()
                            .newsId(news.getNewsId())
                            .title(news.getTitle())
                            .username(news.getMember().getUsername())
                            .image(news.getImage())
                            .view(news.getView())
                            .category(news.getCategory())
                            .createdAt(news.getCreatedAt())
                            .build()
            );
        }
        return new PageImpl(newsList, newslist.getPageable(), newslist.getTotalElements());
    }

    // Category ??? ?????? ??????
    @Transactional(readOnly = true)
    public Page<NewsResponseDto> findAllByCategory(Category category, Pageable pageable) {
        System.out.println("category = " + category);

        Page<News> newsList = newsRepository.findAllByCategoryOrderByCreatedAtDesc(category, pageable);

        Page<NewsResponseDto> newsResponseDtos = convertToResponseDto(newsList);

        return newsResponseDtos;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> readDetailNews(Long newsId) {

//         ????????? data??? ?????? ?????? db?????? ?????? ????????? data??? ????????????.
        String redisNewId = newsId.toString();
//        Optional<NewsRedis> redisNews = newsRedisRepository.findByNewsId(redisNewId); // ????????? ???????????? ?????????
        Optional<NewsRedis> redisNews = newsRedisRepository.findById(redisNewId);
        System.out.println("++++++++++++++111111111111++++++++++++++++");

        if(redisNews.isEmpty()){

            Optional<News> optionalNews = newsRepository.findByNewsId(newsId);
            if(optionalNews.isEmpty()){
                throw new NullPointerException("?????? ???????????? ????????????.");
            }
            News news = optionalNews.get();
            // Redis ??????..
            NewsRedis newsRedis =  NewsRedis.builder()
                    .newsId(news.getNewsId().toString())
                    .title(news.getTitle())
                    .content(news.getContent())
                    .image(news.getImage())
                    .view(news.getView())
                    .category(news.getCategory())
                    .createdAt(news.getCreatedAt())
                    .modifiedAt(news.getModifiedAt())
                    .username(news.getMember().getUsername())
//                    .commentList(news.getCommentList())   //????????? ??????????????? ??????, member db??? ????????? ????????? x ??????????????? DB??????
                    .build();
            System.out.println("22222222222222222newsRedis.getNewsId() = " + newsRedis.getNewsId());
            newsRedisRepository.save(newsRedis);

            List<Comment> commentList = commentRepository.findAllByNews(news);
            List<CommentListDto> commentResponseDtoList = new ArrayList<>();
            for(Comment comment  : commentList){
                commentResponseDtoList.add(
                        CommentListDto.builder()
                                .id(comment.getId())
                                .comment(comment.getComment())
                                .username(comment.getMember().getUsername())
                                .createAt(comment.getCreatedAt())
                                .build());
            }
            NewsDetailResponseDto newsDetailResponseDto = NewsDetailResponseDto.builder()
                    .newsId(news.getNewsId())
                    .title(news.getTitle())
                    .username(news.getMember().getUsername())
                    .image(news.getImage())
                    .view(news.getView())
                    .category(news.getCategory())
                    .createdAt(news.getCreatedAt())
                    .content(news.getContent())
                    .commentList(commentResponseDtoList)
                    .build();

            return new ResponseEntity<>(newsDetailResponseDto, HttpStatus.OK);

        }else {
            System.out.println("++++++++++++++++++++33333333333333333+++++++++++++++++++");
            NewsRedis news = redisNews.get();
            Long redisLongId = Long.parseLong(news.getNewsId());
            List<Comment> commentList = commentRepository.findAllByNewsNewsId(redisLongId);
            List<CommentListDto> commentResponseDtoList = new ArrayList<>();
            for(Comment comment  : commentList){
                commentResponseDtoList.add(
                        CommentListDto.builder()
                                .id(comment.getId())
                                .comment(comment.getComment())
                                .username(comment.getMember().getUsername())
                                .createAt(comment.getCreatedAt())
                                .build());
            }
            NewsDetailResponseDto newsDetailResponseDto = NewsDetailResponseDto.builder()
                    .newsId(redisLongId)
                    .title(news.getTitle())
                    .username(news.getUsername())
                    .image(news.getImage())
                    .view(news.getView())
                    .category(news.getCategory())
                    .createdAt(news.getCreatedAt())
                    .content(news.getContent())
                    .commentList(commentResponseDtoList)
                    .build();

            return new ResponseEntity<>(newsDetailResponseDto, HttpStatus.OK);

        }






/////////////////

//        Optional<News> optionalNews = newsRepository.findByNewsId(newsId);
//        if(optionalNews.isEmpty()){
//            throw new NullPointerException("?????? ???????????? ????????????.");
//        }
//        News news = optionalNews.get();
//
//        List<Comment> commentList = commentRepository.findAllByNews(news);
//        List<CommentListDto> commentResponseDtoList = new ArrayList<>();
//        for(Comment comment  : commentList){
//            commentResponseDtoList.add(
//                CommentListDto.builder()
//                        .id(comment.getId())
//                        .comment(comment.getComment())
//                        .username(comment.getMember().getUsername())
//                        .createAt(comment.getCreatedAt())
//                        .build());
//        }
//
//        NewsDetailResponseDto newsDetailResponseDto = NewsDetailResponseDto.builder()
//                .newsId(news.getNewsId())
//                .title(news.getTitle())
//                .username(news.getMember().getUsername())
//                .image(news.getImage())
//                .view(news.getView())
//                .category(news.getCategory())
//                .createdAt(news.getCreatedAt())
//                .content(news.getContent())
//                .commentList(commentResponseDtoList)
//                .build();
//
//        return new ResponseEntity<>(newsDetailResponseDto, HttpStatus.OK);

    }



    @Transactional
    public ResponseDto<?> deleteNews(Long newsId, HttpServletRequest request) {

        String redisNewId = newsId.toString();
        newsRedisRepository.deleteById(redisNewId);

        // ?????? ?????? ?????? ??????
        Member member = validateMember(request);
        if (null == member) {
            throw new NullPointerException("????????? ?????? ???????????????.");
        }

        // news ????????? ?????? ?????? ?????? ??????
        Optional<News> optionalNews = newsRepository.findByNewsId(newsId);

        if (optionalNews.isEmpty()) {
            return ResponseDto.fail("NOT_FOUND", "???????????? ?????? news ID ?????????.");
        }
        News news = optionalNews.get();  // orElseThorw New~~

        // ?????? ?????? ???????????? ????????? ??????
        if (!news.getMember().getId().equals(member.getId())) {
            return ResponseDto.fail("BAD_REQUEST", "???????????? ????????? ??? ????????????.");
        }

        newsRepository.deleteById(newsId);

        return ResponseDto.success(true);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        String accessToken = resolveToken(request.getHeader("Authorization"));
        if (!tokenProvider.validationToken(accessToken)) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    private String resolveToken(String token){
        if(token.startsWith("Bearer "))
            return token.substring(7);
        throw new RuntimeException("not valid refresh token !!");
    }


//    @Cacheable("News")
//    public NewsRedis getNews(String newsId) {
//        return ;
//    }



}
