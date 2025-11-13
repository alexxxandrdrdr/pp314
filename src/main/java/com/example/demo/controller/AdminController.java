package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.dto.UserEditDto;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.*;


@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final SpringTemplateEngine templateEngine;

    public AdminController(UserService userService, RoleService roleService, SpringTemplateEngine templateEngine) {
        this.userService = userService;
        this.roleService = roleService;
        this.templateEngine = templateEngine;
    }

    @GetMapping
    public ModelAndView showAdminPanel() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("admin/pages/panel");
        return mav;
    }

    @PostMapping(value = "/api/addUser")
    public ResponseEntity<?> createUser(@RequestBody UserEditDto userDto) {
        userService.saveUserFromDto(userDto);
        logger.info("User created successfully: {}", userDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"api/user-info/{id}"})
    public UserEditDto getUserData(@PathVariable Long id) {
        return userService.getUserEditDtoById(id);
    }

    @PatchMapping(value = "api/edit-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestParam String firstname, @RequestParam String lastname, @RequestParam byte age, @RequestParam String email, @RequestParam String password,@RequestParam List<Long> roles) {
        logger.info("Received update user request: {}", email);
        userService.updateUser(id, firstname, lastname, age, email, password, roles);
        return ResponseEntity.ok().build();

    }

    @GetMapping(value = "api/roles")
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }

    @GetMapping(value = "api/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping(value = "api/current-user")
    public User getCurrentUser(@AuthenticationPrincipal User user) {
        return user;
    }

    @DeleteMapping("api/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
