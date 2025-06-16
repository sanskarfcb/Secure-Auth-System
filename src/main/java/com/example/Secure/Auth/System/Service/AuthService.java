package com.example.Secure.Auth.System.Service;


import com.example.Secure.Auth.System.Dtos.LoginRequest;
import com.example.Secure.Auth.System.Dtos.SignupRequest;
import com.example.Secure.Auth.System.Model.User;
import com.example.Secure.Auth.System.Repository.UserRepo;
import com.example.Secure.Auth.System.Security.jwtUtil;
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
    private jwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public  String signup(SignupRequest request){
        if(userRepo.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("User already existed");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail((request.getEmail()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);
        emailService.sendMail(user.getEmail(), "Signup successfull","Hello" + user.getUsername()+" you have successfully signed up.");
        return "User Registered Successfully";
    }
    public String login(LoginRequest request){

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword())
    );



        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("User not found"));
        if(!passwordEncoder.matches(request.getPassword() , user.getPassword()) ){
            throw new RuntimeException("Invalid Credentails");
        }
        emailService.sendMail(user.getEmail(), "Login Notification", "You just logged in at " + LocalDateTime.now());

        return jwtUtil.generateToken(user.getEmail());
    }
}
