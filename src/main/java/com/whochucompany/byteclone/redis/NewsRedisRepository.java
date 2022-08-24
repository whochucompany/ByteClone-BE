package com.whochucompany.byteclone.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NewsRedisRepository extends CrudRepository<NewsRedis, String> {

    Optional<NewsRedis> findByNewsId(String newsId);

}
