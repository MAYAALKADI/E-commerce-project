package com.example.store_project.user.controller;

import com.example.store_project.user.model.User;
import com.example.store_project.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // تسجيل مستخدم جديد
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    // تسجيل الدخول (بالإيميل والباسورد مع تتبع الـ IP وحماية الـ 5 محاولات)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        String ip = request.getRemoteAddr();

        String token = userService.loginUser(email, password, ip);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // عرض كل المستخدمين (خاص بالأدمن)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}