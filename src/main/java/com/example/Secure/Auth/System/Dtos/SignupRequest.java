package com.example.Secure.Auth.System.Dtos;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;
}
