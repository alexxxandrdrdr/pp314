package com.example.demo.controller;

import com.example.demo.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.example.demo.service.RoleService.rolesToString;

@Controller
@RequestMapping("/user")
public class UserController {
    @GetMapping
    public String showUserPage(Model model, @AuthenticationPrincipal User user) {
        String roles = rolesToString(user.getRoles());
        model.addAttribute("authUser", user);
        model.addAttribute("roles", roles);
        return "user/user";
    }
}

