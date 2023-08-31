package com.aman.BlogPost.Controller;

import com.aman.BlogPost.Entity.Comment;
import com.aman.BlogPost.Entity.Post;
import com.aman.BlogPost.Repository.CommentRepository;
import com.aman.BlogPost.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class CommentController {
    
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;


    @PostMapping("/addComment")
    public String createComment(Model model, @RequestParam("postId") int postId,
                                @ModelAttribute Comment comment){

        Post post = postRepository.findById(postId).orElse(null);
        if(post != null){
            comment.setPost(post);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);

            model.addAttribute("commentAdded", true);
        }
        return "redirect:/view/" + postId;
    }


    @GetMapping("/editComment/{id}")
    public String editCommentForm(@PathVariable int id, Model model){
        Comment comment = commentRepository.findById(id).orElse(null);
        if(comment != null){
            model.addAttribute("comment",comment);
            return "editComment";
        }else{
            return "redirect:/view/" + id;
        }
    }

    @PostMapping("/updateComment")
    public String updateComment(@ModelAttribute Comment comment){
        Comment existingComment = commentRepository.findById(comment.getId()).orElse(null);
        if(existingComment != null){
            existingComment.setName(comment.getName());
            existingComment.setEmail(comment.getEmail());
            existingComment.setComment(comment.getComment());
            existingComment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(existingComment);
        }
        return "redirect:/view/" + existingComment.getPostId();
    }

    @GetMapping("/deleteComment/{id}")
    public String deleteComment(@PathVariable int id){
        Comment comment = commentRepository.findById(id).orElse(null);
        int postId = comment.getPostId();
        commentRepository.delete(comment);
        return "redirect:/view/" + postId;
    }
}
