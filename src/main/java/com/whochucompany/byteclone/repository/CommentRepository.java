package com.whochucompany.byteclone.repository;

import com.whochucompany.byteclone.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}