package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.RoleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {
    private final RoleService roleService;

    public UserController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String showUserPage(Model model, @AuthenticationPrincipal User user) {
        String roles = roleService.rolesToString(user.getRoles());
        model.addAttribute("authUser", user);
        model.addAttribute("roles", roles);
        return "user/user";
    }
}

