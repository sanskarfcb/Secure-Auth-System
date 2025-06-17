package com.example.Secure.Auth.System.Controller;

import com.example.Secure.Auth.System.Dtos.LoginRequest;
import com.example.Secure.Auth.System.Dtos.SignupRequest;
import com.example.Secure.Auth.System.Service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request, HttpServletResponse response) {
        String result = authService.signup(request, response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String result = authService.login(request, response);
        return ResponseEntity.ok(result);
    }
}