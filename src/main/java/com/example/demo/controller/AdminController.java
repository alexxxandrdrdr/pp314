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

import static com.example.demo.service.RoleService.rolesToString;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String showAdminPanel(Model model, @AuthenticationPrincipal User user) {
        String roles = rolesToString(user.getRoles());
        model.addAttribute("authRoles", roles);
        model.addAttribute("authUser", user);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("usersList", userService.findAll());
        return "admin/panel";
    }


    @PostMapping("/addUser")
    public String createUser(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit-user/{id}")
    @ResponseBody
    public Map<String, Object> getUserForEdit(@PathVariable Long id) {
        User user = userService.findById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("roleIds", user.getRoles().stream()
                .map(Role::getId)
                .toList());

        return response;
    }

    @PatchMapping("/edit-user/{id}")
    @ResponseBody
    public Map<String, String> updateUser(@PathVariable Long id, @RequestBody UserEditDto userEditDto) {
        if (userEditDto!= null && id != null) {
            userService.updateUser(userEditDto,id);
        } else throw new IllegalArgumentException("Изменений нет");
        return Map.of("status", "success");
    }

    @GetMapping("/user-info/{id}")
    @ResponseBody
    public Map<String, Object> getUserInfo(@PathVariable Long id) {
        User user = userService.findById(id);
        Map<String, Object> response = new HashMap<>();

        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("roleNames", user.getRoles().stream()
                .map(Role::getName)
                .toList());
        return response;
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
        return  ResponseEntity.ok("User deleted successfully");
    }
}
