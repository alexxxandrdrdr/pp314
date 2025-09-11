package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserEditDto;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

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
    public String showAdminPanel(Model model, @AuthenticationPrincipal User user) {
        String roles = roleService.rolesToString(user.getRoles());
        model.addAttribute("authRoles", roles);
        model.addAttribute("authUser", user);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("usersList", userService.findAll());
        return "admin/panel";
    }


    @PostMapping("/addUser")
    public String createUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin";
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

    @DeleteMapping("/delete-user/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
