package com.example.demo.controller;

import com.example.demo.model.User;

import com.example.demo.service.RoleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/user")
public class UserController {

    private final RoleService roleService;

    public UserController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ModelAndView showUserPage(@AuthenticationPrincipal User currentUser) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("currentUser", currentUser);
        mav.addObject("roles", roleService.findAll());
        mav.setViewName("panel");
        return mav;

    }
}

