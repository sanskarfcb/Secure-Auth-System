package com.example.Secure.Auth.System.Service;

import com.example.Secure.Auth.System.Dtos.LoginRequest;
import com.example.Secure.Auth.System.Dtos.SignupRequest;
import com.example.Secure.Auth.System.Model.User;
import com.example.Secure.Auth.System.Repository.UserRepo;
import com.example.Secure.Auth.System.Security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String signup(SignupRequest request, HttpServletResponse response) {
        // Trim input
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String username = request.getUsername() != null ? request.getUsername().trim() : "";
        String password = request.getPassword() != null ? request.getPassword().trim() : "";

        // Validation checks
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return "Email, Username, and Password must be provided";
        }

        if (!email.contains("@")) {
            return "Invalid email format. Email must contain '@'";
        }

        if (password.length() < 4) {
            return "Password must be at least 4 characters long";
        }

        if (userRepo.findByEmail(email).isPresent()) {
            return "User already exists with this email";
        }

        // Check for duplicate username (optional)
        if (userRepo.findAll().stream().anyMatch(user -> user.getUsername().equalsIgnoreCase(username))) {
            return "Username already taken";
        }

        // Save new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);

        // Generate JWT and set in cookie
        String token = jwtUtil.generateToken(user.getEmail());

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        emailService.sendMail(user.getEmail(), "Signup Successful",
                "Hello " + user.getUsername() + ", you have successfully signed up.");

        return "User Registered Successfully";
    }

    public String login(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String password = request.getPassword() != null ? request.getPassword().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            return "Email and password must be provided";
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            return "Invalid email or password";
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        emailService.sendMail(user.getEmail(), "Login Notification",
                "You just logged in at " + LocalDateTime.now());

        return "User login successful";
    }
}