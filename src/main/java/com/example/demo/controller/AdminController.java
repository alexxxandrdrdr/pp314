package com.example.demo.controller;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    private record UserResponse(Long id, String firstname, String lastname, Byte age, String email,
                                List<Long> roleIds) {
        static UserResponse fromUser(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getAge(),
                    user.getEmail(),
                    user.getRoles().stream().map(Role::getId).toList()
            );
        }
    }

    @GetMapping
    public String showAdminPanel() {
        return "admin/panel";
    }

    @PostMapping("/addUser")
    @ResponseBody
    public ResponseEntity<String> createUser(@RequestBody UserEditDto userDto) {
        logger.info("Received create user request: {}", userDto);
        try {
            userService.saveUserFromDto(userDto);
            logger.info("User created successfully: {}", userDto.getEmail());
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            logger.error("Failed to create user: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body("Failed to create user: " + e.getMessage());
        }
    }

    @GetMapping({"/edit-user/{id}", "/user-info/{id}"})
    @ResponseBody
    public ResponseEntity<?> getUserData(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }


    @PatchMapping("/edit-user/{id}")
    @ResponseBody
    public Map<String, String> updateUser(@PathVariable Long id, @RequestBody UserEditDto userEditDto) {
        try {
            userService.updateUser(userEditDto, id);
        } catch (Exception e) {
            return Map.of("status", "failed");
        }
        return Map.of("status", "success");
    }


    @GetMapping("/roles")
    @ResponseBody
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }

    @GetMapping("/users")
    @ResponseBody
    public List<UserResponse> getAllUsers() {
        return userService.findAll().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/current-user")
    @ResponseBody
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }


    @DeleteMapping("/delete-user/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
