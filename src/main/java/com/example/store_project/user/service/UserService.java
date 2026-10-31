package com.example.store_project.user.service;

import com.example.store_project.security.JwtService;
import com.example.store_project.user.model.User;
import com.example.store_project.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerUser(User user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "جميع الحقول (اسم المستخدم، الإيميل، كلمة المرور) مطلوبة");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "البريد الإلكتروني مسجل مسبقاً");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        
        user.setFailedAttempt(0);
        user.setAccountNonLocked(true);
        
        return userRepository.save(user);
    }

public String loginUser(String email, String password, String ip) {
        if (email == null || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "يرجى تزويد البريد الإلكتروني وكلمة المرور");
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "الإيميل غير مسجل أساساً!");
        }

        // 1. التحقق هل الحساب محظور حالياً؟ وهل انتهت دقيقة الحظر أم لا؟
        if (!user.isAccountNonLocked()) {
            // التحقق إذا مر أكثر من دقيقة على وقت الحظر
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (user.getLockTime() != null && user.getLockTime().plusMinutes(1).isBefore(now)) {
                // إذا مرّت الدقيقة، نفك الحظر ونصفر العداد تلقائياً!
                user.setAccountNonLocked(true);
                user.setFailedAttempt(0);
                user.setLockTime(null);
                userRepository.save(user);
            } else {
                // إذا لسا الدقيقة ما خلصت، نمنعه ونخبره
                throw new ResponseStatusException(HttpStatus.LOCKED, "تم حظر حسابك مؤقتاً بسبب كثرة المحاولات. يرجى المحاولة بعد دقيقة واحدة.");
            }
        }

        // 2. التحقق من الباسورد
        if (!passwordEncoder.matches(password, user.getPassword())) {
            int currentAttempts = user.getFailedAttempt() + 1;
            user.setFailedAttempt(currentAttempts);

            if (currentAttempts >= 5) {
                user.setAccountNonLocked(false);
                user.setLockTime(java.time.LocalDateTime.now()); // تسجيل وقت الحظر الحالي
                userRepository.save(user);
                throw new ResponseStatusException(HttpStatus.LOCKED, "تم حظر حسابك لمدة دقيقة واحدة بسبب تجاوز 5 محاولات خاطئة!");
            }

            userRepository.save(user);
            int remaining = 5 - currentAttempts;
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "كلمة المرور خطأ! متبقي لديك " + remaining + " محاولات.");
        }

        // 3. إذا دخل صح، نُصفر العداد ونلغي أي حظر سابق
        if (user.getFailedAttempt() > 0 || !user.isAccountNonLocked()) {
            user.setFailedAttempt(0);
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            userRepository.save(user);
        }

        return jwtService.generateToken(user.getUsername(), user.getRole());
    }
}