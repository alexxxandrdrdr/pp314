package com.example.demo.controller;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserEditDto;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;



@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final UserMapper userMapper;

    public AdminController(UserService userService, RoleService roleService, UserMapper userMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String showAdminPanel() {
        return "admin/pages/panel";
    }

    @ResponseBody
    @PostMapping(value = "/api/addUser")
    public ResponseEntity<?> createUser(@RequestBody UserEditDto userDto) {
        userService.saveUserFromDto(userDto);
        logger.info("User created successfully: {}", userDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @GetMapping(value = {"/api/user-info/{id}"})
    public UserEditDto getUserData(@PathVariable Long id) {
        return userMapper.mapUserToUserEditDto(userService.findById(id));
    }

    @ResponseBody
    @PatchMapping(value = "/api/edit-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestParam String firstname, @RequestParam String lastname, @RequestParam byte age, @RequestParam String email, @RequestParam String password,@RequestParam List<Long> roles) {
        logger.info("Received update user request: {}", email);
        userService.updateUser(id, firstname, lastname, age, email, password, roles);
        return ResponseEntity.ok().build();

    }

    @ResponseBody
    @GetMapping(value = "/api/roles")
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }
    @ResponseBody
    @GetMapping(value = "/api/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @ResponseBody
    @GetMapping(value = "/api/current-user")
    public User getCurrentUser(@AuthenticationPrincipal User user) {
        return userService.findById(user.getId()).orElse(null);
    }

    @ResponseBody
    @DeleteMapping("/api/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
