package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.RoleService;
import jakarta.servlet.ServletContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@RestController
@RequestMapping("/user")
public class UserController {
    private final RoleService roleService;
    private final SpringTemplateEngine templateEngine;


    public UserController(RoleService roleService, SpringTemplateEngine templateEngine) {
        this.roleService = roleService;
        this.templateEngine = templateEngine;
    }

    @GetMapping
    public ResponseEntity<String> showUserPage(Model model, @AuthenticationPrincipal User user) {
Context context = new Context();
        context.setVariable("authUser",user);
        context.setVariable("roles", roleService.findAll());
        context.setVariable("activeRole", "ROLE_USER");
        String html =templateEngine.process("user/user",context);

        return ResponseEntity.ok(html);

    }
}

