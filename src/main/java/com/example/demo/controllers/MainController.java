package com.example.demo.controllers;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MainController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User userHTML, Map<String, Object> model) {
        User userFromDb = userRepo.findByEmail(userHTML.getEmail());
        if (userFromDb != null) {
            model.put("text", "User exists!");
            return "registration";
        }
        userHTML.setActive(true);
        userHTML.setDateOfRegistration(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        userHTML.setRoles(Collections.singleton(Role.USER));
        userRepo.save(userHTML);
        return "redirect:/login";
    }


    @GetMapping("/")
    public String main(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {
        if (logout(request, response))
            return "redirect:/login";
        Iterable<User> users = userRepo.findAll();
        model.put("users", users);

        return "main";
    }

    @PostMapping(value = "/edit", params = "block=block")
    public String blockUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("idChecked") String[] emails, Map<String, Object> model) {
        if (logout(request, response))
            return "redirect:/login";
        for (String email : emails) {
            User user = userRepo.findByEmail(email);
            user.setActive(false);
            userRepo.save(user);
        }
        if (logout(request, response, emails))
            return "redirect:/login";
        Iterable<User> users = userRepo.findAll();
        model.put("users", users);
        return "main";
    }

    @PostMapping(value = "/edit", params = "delete")
    public String deleteUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("idChecked") String[] emails, Map<String, Object> model) {
        if (logout(request, response))
            return "redirect:/login";
        for (String email : emails) {
            userRepo.deleteByEmail(email);
        }
        if (logout(request, response, emails))
            return "redirect:/login";
        Iterable<User> users = userRepo.findAll();
        model.put("users", users);
        return "main";
    }


    @PostMapping(value = "/edit", params = "unblock")
    public String unblockUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("idChecked") String[] emails, Map<String, Object> model) {
        if (logout(request, response))
            return "redirect:/login";
        for (String email : emails) {
            User user = userRepo.findByEmail(email);
            user.setActive(true);
            userRepo.save(user);
        }
        Iterable<User> users = userRepo.findAll();
        model.put("users", users);
        return "main";
    }

    private boolean logout(HttpServletRequest request, HttpServletResponse response) {
        var authenticator = SecurityContextHolder.getContext().getAuthentication();
        if (authenticator.isAuthenticated() && (userRepo.findByEmail(authenticator.getName()) == null || !userRepo.findByEmail(authenticator.getName()).isActive())) {
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            return true;
        }

        return false;
    }

    private boolean logout(HttpServletRequest request, HttpServletResponse response, String[] users) {
        SecurityContextHolder.getContext().getAuthentication().getName();
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && Arrays.asList(users).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            return true;
        }
        return false;
    }

}
