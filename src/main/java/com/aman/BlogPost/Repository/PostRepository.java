package com.aman.BlogPost.Repository;

import com.aman.BlogPost.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND " +
            "(:search IS NULL OR p.title LIKE %:search% OR p.author LIKE %:search% " +
            "OR p.content LIKE %:search%)")
    Page<Post> searchByTitleOrAuthor(String search, Pageable pageable);

    @Query("SELECT DISTINCT p.author FROM Post p WHERE p.isPublished = true")
    List<String> findAllAuthors();

    @Query("SELECT p from Post p WHERE p.isPublished = true")
    Page<Post> findAllPublishPost(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isPublished = false")
    List<Post> findByAllDrafts();

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND p.author IN :author")
    Page<Post> findByAuthor(List<String> author, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.isPublished = true AND t.name IN :tagName")
    Page<Post> findByTag(List<String> tagName, Pageable pageable);


    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.isPublished = true AND (t.name IN :tagName" +
            " OR p.author IN :authorName)")
    Page<Post> findByAuthorAndTag(List<String> authorName, List<String> tagName, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isPublished = false AND p.author = :loggedInUser")
    List<Post> findByAllDraftsByloggedInUser(String loggedInUser);

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND p.author = :loggedInUser")
    List<Post> findByAllPublishedPostByloggedInUser(String loggedInUser);

    @Query("SELECT p FROM Post p WHERE p.publishedAt BETWEEN :dateFrom AND :dateTo")
    Page<Post> findByDateRange(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable
    );
}