package com.aman.BlogPost.Controller;

import com.aman.BlogPost.Entity.Comment;
import com.aman.BlogPost.Entity.Post;
import com.aman.BlogPost.Entity.Tag;
import com.aman.BlogPost.Repository.CommentRepository;
import com.aman.BlogPost.Repository.PostRepository;
import com.aman.BlogPost.Repository.TagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping({"/view/{id}", "/viewDraft/{id}"})
    public String viewPost(@PathVariable int id, Model model) {
        Post post = postRepository.findById(id).orElse(null);
        List<Comment> comments = commentRepository.findByPostId(id);
        List<Tag> tags = post.getTags();

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("tags", tags);

        return "viewPost";
    }

    @GetMapping("/createForm")
    public String createPostForm(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();

        Post post = new Post();
        model.addAttribute("post", post);
        model.addAttribute("loggedInUser",loggedInUser);
        return "createPost";
    }

    @PostMapping("/publishOrDraft")
    public String createPost(@ModelAttribute Post post, @RequestParam("tag") String tags,
                             @RequestParam("postCreated") String action) {

        String[] tagList = tags.split(",");

        for (String tagName : tagList) {
            Tag existingTag = tagRepository.findByName(tagName.trim());
            if (existingTag == null) {
                Tag tag = new Tag();
                tag.setName(tagName.trim());
                tag.setCreatedAt(LocalDateTime.now());
                tag.setUpdatedAt(LocalDateTime.now());
                tagRepository.save(tag);
                post.getTags().add(tag);
            } else {
                post.getTags().add(existingTag);
            }
        }
        if ("draftPost".equals(action)) {
            post.setCreatedAt(LocalDate.now());
            post.setUpdatedAt(LocalDate.now());
            postRepository.save(post);
            return "redirect:/Draft";
        } else {
            post.setPublished(true);
            post.setPublishedAt(LocalDate.now());
            post.setCreatedAt(LocalDate.now());
            post.setUpdatedAt(LocalDate.now());
            postRepository.save(post);
            return "redirect:/posts";
        }
    }

    @GetMapping({"/edit/{id}", "/editDraft/{id}"})
    public String editPostForm(@PathVariable int id, Model model) {
        Post post = postRepository.findById(id).orElse(null);
        StringJoiner tagNames = new StringJoiner(", ");
        for (Tag tag : post.getTags()) {
            tagNames.add(tag.getName());
        }
        String result = tagNames.toString();

        model.addAttribute("post", post);
        model.addAttribute("tagNames", result);
        return "editPost";
    }

    @PostMapping("/posts/update")
    public String updatePost(@ModelAttribute Post post, @RequestParam("tag") String tags) {
        Post existingPost = postRepository.findById(post.getId()).orElse(null);
        if (existingPost != null) {
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
            existingPost.setAuthor(post.getAuthor());

            String[] tagList = tags.split(",");
            List<Tag> updatedTags = new ArrayList<>();

            for (String tagName : tagList) {
                tagName = tagName.trim();
                Tag tag = tagRepository.findByName(tagName);

                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tag.setCreatedAt(LocalDateTime.now());
                    tag.setUpdatedAt(LocalDateTime.now());
                    tagRepository.save(tag);
                }

                updatedTags.add(tag);
            }

            existingPost.getTags().clear();
            existingPost.getTags().addAll(updatedTags);

            existingPost.setUpdatedAt(LocalDate.now());
            postRepository.save(existingPost);
        }
        return "redirect:/posts";
    }

    @PostMapping("/posts/updateDraft")
    public String updateDraftPost(@ModelAttribute Post post, @RequestParam("tag") String tags) {
        Post existingPost = postRepository.findById(post.getId()).orElse(null);
        if (existingPost != null) {
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
            existingPost.setAuthor(post.getAuthor());

            String[] tagList = tags.split(", ");
            List<Tag> updatedTags = new ArrayList<>();

            for (String tagName : tagList) {
                tagName = tagName.trim();
                Tag tag = tagRepository.findByName(tagName);

                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tag.setCreatedAt(LocalDateTime.now());
                    tag.setUpdatedAt(LocalDateTime.now());
                    tagRepository.save(tag);
                }

                updatedTags.add(tag);
            }

            existingPost.getTags().clear();
            existingPost.getTags().addAll(updatedTags);

            existingPost.setUpdatedAt(LocalDate.now());
            postRepository.save(existingPost);
        }
        return "redirect:/Draft";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable int id) {
        postRepository.deleteById(id);
        return "redirect:/posts";
    }

    @GetMapping("/deleteDraft/{id}")
    public String deleteDraftPost(@PathVariable int id) {
        postRepository.deleteById(id);
        return "redirect:/Draft";
    }

    @GetMapping("/Draft")
    public String savedAsDraft(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();

        List<Post> userDrafts = postRepository.findByAllDraftsByloggedInUser(loggedInUser);
        for (Post draft : userDrafts) {
            List<Tag> tags = tagRepository.findByPost(draft);
            draft.setTags(tags);
        }

        model.addAttribute("allDrafts", userDrafts);
        return "draftPost";
    }

    @GetMapping("/allPublishedPost")
    public String allPublishedPostOfLoggedInUser(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();

        List<Post> allPublishedPost = postRepository.findByAllPublishedPostByloggedInUser(loggedInUser);
        for (Post post : allPublishedPost) {
            List<Tag> tags = tagRepository.findByPost(post);
            post.setTags(tags);
        }

        model.addAttribute("allPublishPost", allPublishedPost);
        return "myPublishedPost";
    }

    @GetMapping({"/posts", "/"})
    public String getAllPosts(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "publishedAt") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(value = "author", required = false) List<String> authorFilter,
            @RequestParam(value = "tag", required = false) List<String> tagFilter,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = authentication.getName();

        if (page < 0) {
            page = 0;
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Post> postPage;

        if (search != null && !search.isEmpty()) {
            postPage = postRepository.searchByTitleOrAuthor(search, pageable);
        } else {
            postPage = postRepository.searchByTitleOrAuthor(null, pageable);
        }

        if (authorFilter != null && !authorFilter.isEmpty()) {
            if (tagFilter != null && !tagFilter.isEmpty()) {
                postPage = postRepository.findByAuthorAndTag(authorFilter, tagFilter, pageable);
            } else {
                postPage = postRepository.findByAuthor(authorFilter, pageable);
            }
        } else if (tagFilter != null && !tagFilter.isEmpty()) {
            postPage = postRepository.findByTag(tagFilter, pageable);
        }

        if (dateFrom != null && dateTo != null) {
            postPage = postRepository.findByDateRange(dateFrom, dateTo, pageable);
        }

        List<String> allAuthors = postRepository.findAllAuthors();
        List<String> allTags = tagRepository.findAllTags();
        List<Post> posts = postPage.getContent();

        for (Post post : posts) {
            List<Tag> tags = tagRepository.findByPost(post);
            post.setTags(tags);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("postPage", postPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("tagFilter", tagFilter);
        model.addAttribute("authorFilter", authorFilter);
        model.addAttribute("authors", allAuthors);
        model.addAttribute("tags", allTags);
        model.addAttribute("loggedInUser", loggedInUser);

        return "postList";
    }

    @GetMapping("/publish/{id}")
    public String publishPostFromDraft(@PathVariable int id) {
        Post post = postRepository.findById(id).orElse(null);
        post.setPublished(true);
        post.setPublishedAt(LocalDate.now());
        postRepository.save(post);
        return "redirect:/posts";
    }
}
