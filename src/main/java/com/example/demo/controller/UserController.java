package com.example.demo.controller;

import com.example.demo.model.User;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public ModelAndView showUserPage(@AuthenticationPrincipal User user) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("authUser", user);
        mav.addObject("roles", user.getRoles());
        mav.addObject("activeRole", "ROLE_USER");
        mav.setViewName("user/user");
        return mav;

    }
}

