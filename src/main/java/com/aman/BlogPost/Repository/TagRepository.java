package com.aman.BlogPost.Repository;

import com.aman.BlogPost.Entity.Post;
import com.aman.BlogPost.Entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Integer> {

    Tag findByName(String name);

    List<Tag> findByPost(Post post);

    @Query("SELECT DISTINCT t.name FROM Tag t JOIN t.post p WHERE p.isPublished = true")
    List<String> findAllTags();
}
