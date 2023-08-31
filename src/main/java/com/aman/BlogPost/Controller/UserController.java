package com.aman.BlogPost.Controller;

import com.aman.BlogPost.Entity.User;
import com.aman.BlogPost.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/loginForm")
    public String createLoginForm() {
        return "login";
    }

    @GetMapping("/signUpForm")
    public String createSignUpForm() {
        return "signUp";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam("confirm-password") String confirmPassword,
                               Model model) {

        if (confirmPassword.equals(user.getPassword()) == false) {
            model.addAttribute("error", "Password Mismatch");
            return "signUp";
        } else {
            String password = user.getPassword();
            user.setPassword("{noop}" + password);
            user.setAuthority("ROLE_AUTHOR");
            user.setEnabled(true);
            userRepository.save(user);
            return "redirect:/loginForm";
        }
    }
}
