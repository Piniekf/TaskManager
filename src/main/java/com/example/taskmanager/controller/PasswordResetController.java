package com.example.taskmanager.controller;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.UUID;

@Controller
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @GetMapping("/reset_password")
    public String resetPasswordForm() {
        return "reset_password";
    }

    @PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        // Sprawdź, czy istnieje użytkownik o podanym adresie e-mail
        User user = userService.findUserByEmail(email);

        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Wygeneruj unikalny token resetowania hasła
        String resetToken = generateResetToken();

        // Aktualizuj token resetowania hasła w bazie danych w rekordzie użytkownika
        user.setResetPasswordToken(resetToken);
        userService.updateUser(user); // Użyj metody updateUser, aby dostosować logikę

        // Wyślij e-mail z linkiem resetowania hasła, zawierającym token
        sendPasswordResetEmail(user, resetToken);

        return new ResponseEntity<>("Password reset email sent", HttpStatus.OK);
    }

    private String generateResetToken() {
        // Generowanie unikalnego tokenu, np. za pomocą UUID
        return UUID.randomUUID().toString();
    }

    private void sendPasswordResetEmail(User user, String resetToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Reset Password");
        mailMessage.setText("Click the following link to reset your password: http://localhost:8080/reset_password?token=" + resetToken);

        javaMailSender.send(mailMessage);
    }
}
