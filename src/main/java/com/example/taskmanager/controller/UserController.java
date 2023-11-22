package com.example.taskmanager.controller;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class UserController {
    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
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

        // Sprawdź błędy walidacji hasła
        if(result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        // Sprawdź walidację hasła
        if (!isPasswordValid(userDto.getPassword())) {
            result.rejectValue("password", null,
                    "Hasło musi mieć co najmniej 8 znaków, zawierać co najmniej jedną małą literę, jedną wielką literę, jedną cyfrę i jeden znak specjalny.");
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam String token) {
        User user = userService.findUserByActivationToken(token);
        if (user != null && user.isActivatedTokenValid()) {
            user.setIsActivated(true);
            user.setIsActivatedExpiryDate(null);
            user.setIsActivatedToken(null);
            userService.updateUser(user);
            return "/login";
        }

        return "/login";
    }
    @GetMapping("/users/")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/users/block/{email}")
    public String blockUser(@PathVariable String email) {
        User user = userService.findUserByEmail(email);
        if (user != null) {
            userService.blockUserByEmail(user.getEmail());
        }
        return "redirect:/users/";
    }

    @GetMapping("/users/unblock/{email}")
    public String unblockUser(@PathVariable String email) {
        User user = userService.findUserByEmail(email);
        if (user != null) {
            userService.unblockUserByEmail(user.getEmail());
        }
        return "redirect:/users/";
    }
    private boolean isPasswordValid(String password) {
        // Wymagania: co najmniej 8 znaków, przynajmniej jedna mała litera, jedna wielka litera, jedna cyfra i jeden znak specjalny
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";
        return password.matches(regex);
    }

}