package com.example.taskmanager.controller;

import jakarta.validation.Valid;
import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender javaMailSender;
    private static final int TOKEN_EXPIRATION_MINUTES = 10; // Ważność tokenu

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model){
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "Istnieje już konto z tym adresem e-mail");
        }

        if(result.hasErrors()){
            model.addAttribute("user", userDto);
            return "/register";
        }
        String activationToken = generateActiveToken();
        userService.saveUser(userDto);
        sendActivationEmail(userDto.getEmail(), activationToken);
        return "redirect:/register?success";
    }
    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token) {
        User user = userService.findUserByActivationToken(token);

        if (user != null) {
            // Oznacz konto jako aktywowane
            user.setIsActivated(true);
            userService.updateUser(user);
            return "/login";
        }

        return "/login"; //
    }
    @GetMapping("/users")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    private void sendActivationEmail(String email, String activationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("taskmanagerautomat@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("Aktywacja konta w TaskManager");
        mailMessage.setText("Kliknij w link, aby aktywować swoje konto: http://localhost:8080/activate?token=" + activationToken);
        javaMailSender.send(mailMessage);
    }
    private String generateActiveToken() {
        return UUID.randomUUID().toString();
    }
}