package com.example.demo.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class Securityhandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserRepo userRepo;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        var user = userRepo.findByEmail(authentication.getName());
        user.setDateOfLastLogin(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        userRepo.save(user);
        response.sendRedirect("/");
    }
}