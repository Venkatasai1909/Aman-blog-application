package com.aman.BlogPost.Repository;

import com.aman.BlogPost.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {

    List<Comment> findByPostId(int postId);
}
