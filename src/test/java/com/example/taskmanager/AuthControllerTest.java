package com.example.taskmanager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskmanager.controller.AuthController;
import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

class AuthControllerTest {

    private AuthController authController;
    private UserService userService;
    private Model model;
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        model = mock(Model.class);
        bindingResult = mock(BindingResult.class);
        authController = new AuthController(userService);
    }

    @Test
    void testShowRegistrationForm() {
        assertEquals("register", authController.showRegistrationForm(model));
        verify(model).addAttribute(eq("user"), any(UserDto.class));
    }

    @Test
    void testRegistrationSuccess() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        when(userService.findUserByEmail(anyString())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        assertEquals("redirect:/register?success", authController.registration(userDto, bindingResult, model));
        verify(userService).saveUser(userDto);
    }

    @Test
    void testRegistrationWithExistingEmail() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        when(userService.findUserByEmail(anyString())).thenReturn(new User());
        when(bindingResult.hasErrors()).thenReturn(false);

        assertEquals("/register", authController.registration(userDto, bindingResult, model));
        verify(model).addAttribute(eq("user"), any(UserDto.class));
        verify(bindingResult).rejectValue(eq("email"), anyString(), anyString());
    }

    @Test
    void testRegistrationWithBindingErrors() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        when(bindingResult.hasErrors()).thenReturn(true);

        assertEquals("/register", authController.registration(userDto, bindingResult, model));
        verify(model).addAttribute(eq("user"), any(UserDto.class));
    }

    @Test
    void testActivateAccountInvalidToken() {
        when(userService.findUserByActivationToken(anyString())).thenReturn(null);

        assertEquals("/login", authController.activateAccount("invalidToken"));
    }
}
