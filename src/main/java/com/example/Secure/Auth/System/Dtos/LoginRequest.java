package com.example.Secure.Auth.System.Dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
