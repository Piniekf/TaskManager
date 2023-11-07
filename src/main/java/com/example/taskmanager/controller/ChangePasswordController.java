package com.example.taskmanager.controller;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class ChangePasswordController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/change_password")
    public String changePasswordForm(@RequestParam String token, Model model) {
        // Sprawdź, czy token jest poprawny
        User user = userService.findUserByResetPasswordToken(token);

        if (user != null && user.isResetPasswordTokenValid()) {
            model.addAttribute("token", token);
            return "change_password";
        } else {
            return "tu by się przydało wstawić stronę jak token jest nieprawidłowy";
        }
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam String password1, @RequestParam String password2, @RequestParam String token, Model model) {
        // Sprawdź, czy token jest poprawny
        User user = userService.findUserByResetPasswordToken(token);

        if (user != null && user.isResetPasswordTokenValid()) {
            if (password1.equals(password2)) {
                // Jeśli hasła są identyczne, wykonaj zmianę hasła
                String encodedPassword = passwordEncoder.encode(password1); // Kodowanie nowego hasłą
                user.setPassword(encodedPassword);
                user.setResetPasswordToken(null); // Czyści token w bazie
                user.setResetPasswordTokenExpiryDate(null); // Czyści datę ważnosci tokenu
                userService.updateUser(user);

                // Przekieruj użytkownika na stronę logowania po pomyślnym zresetowaniu hasła
                return "redirect:/login";
            } else {
                // Hasła nie pasują do siebie, przekieruj użytkownika z powrotem do formularza z błędem
                model.addAttribute("token", token);
                model.addAttribute("error", true);
                return "change_password";
            }
        } else {
            // Jeśli token jest niepoprawny lub nieważny, przekieruj na stronę błędu lub inny widok
            return "tu by się przydało wstawić stronę jak token jest nieprawidłowy";
        }
    }
}
