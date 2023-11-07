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
import java.util.Calendar;
import java.util.Date;

@Controller
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;
    private static final int TOKEN_EXPIRATION_MINUTES = 10; // Ważność tokenu

    @GetMapping("/reset_password")
    public String resetPasswordForm() {
        return "reset_password";
    }

    @PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        // Sprawdzanie czy instnieje user
        User user = userService.findUserByEmail(email);

        if (user == null) {
            return new ResponseEntity<>("Użytkownik nie znaleziony", HttpStatus.NOT_FOUND);
        }

        // Generuje token
        String resetToken = generateResetToken();

        // Ustal datę ważności tokenu (obecny czas + 10 minut)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, TOKEN_EXPIRATION_MINUTES);
        Date tokenExpiryDate = calendar.getTime();

        // Aktualizuj token resetowania hasła w bazie danych w rekordzie użytkownika
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiryDate(tokenExpiryDate);
        userService.updateUser(user); // Użyj metody updateUser, aby dostosować logikę

        // Wyślij e-mail z linkiem resetowania hasła, zawierającym token
        sendPasswordResetEmail(user, resetToken);

        return new ResponseEntity<>("Wysłano mail resetujący!", HttpStatus.OK);
    }

    private String generateResetToken() {
        // Generowanie unikalnego tokenu, np. za pomocą UUID
        return UUID.randomUUID().toString();
    }

    private void sendPasswordResetEmail(User user, String resetToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("TaskManager - Zresetuj hasło");
        mailMessage.setText("Kliknij w link aby zresetować hasło: http://localhost:8080/change_password?token=" + resetToken);

        javaMailSender.send(mailMessage);
    }
}
