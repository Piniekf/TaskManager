package com.example.taskmanager;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.RoleRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private UserServiceImpl userService;

    // Sample user details
    private final String firstName = "John";
    private final String lastName = "Doe";
    private final String email = "john.doe@example.com";
    private final String password = "password";
    private final String encodedPassword = "encodedPassword";
    private final String activationToken = UUID.randomUUID().toString();
    private final String resetPasswordToken = UUID.randomUUID().toString();

    private User user;
    private UserDto userDto;
    private Role role;

    @BeforeEach
    void setUp() {
        // Create a userDto and User instance with sample data for testing
        userDto = new UserDto();
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setPassword(password);

        user = new User();
        user.setName(firstName + " " + lastName);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setIsActivatedToken(activationToken);
        user.setIsActivatedExpiryDate(new Date());

        role = new Role();
        role.setName("ROLE_USER");
    }

    @Test
    void testSaveUser() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(roleRepository.findByName(anyString())).thenReturn(role);

        // When
        userService.saveUser(userDto);

        // Then
        verify(userRepository).save(any(User.class));
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testFindUserByEmail() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // When
        User foundUser = userService.findUserByEmail(email);

        // Then
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    void testFindAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // When
        List<UserDto> userDtos = userService.findAllUsers();

        // Then
        assertEquals(1, userDtos.size());
        assertEquals(firstName, userDtos.get(0).getFirstName());
        assertEquals(lastName, userDtos.get(0).getLastName());
    }

    @Test
    void testFindUserByResetPasswordToken() {
        // Given
        when(userRepository.findByResetPasswordToken(anyString())).thenReturn(user);

        // When
        User foundUser = userService.findUserByResetPasswordToken(resetPasswordToken);

        // Then
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    void testFindUserByActivationToken() {
        // Given
        when(userRepository.findByIsActivatedToken(anyString())).thenReturn(user);

        // When
        User foundUser = userService.findUserByActivationToken(activationToken);

        // Then
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    void testUpdateUser() {
        // When
        userService.updateUser(user);

        // Then
        verify(userRepository).save(user);
    }

    @Test
    void testIsActivationTokenValid() {
        // Given
        when(userRepository.findByIsActivatedToken(anyString())).thenReturn(user);

        // When
        boolean isValid = userService.isActivationTokenValid(activationToken);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testBlockUserByEmail() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // When
        userService.blockUserByEmail(email);

        // Then
        verify(userRepository).save(user);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testUnblockUserByEmail() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // When
        userService.unblockUserByEmail(email);

        // Then
        verify(userRepository).save(user);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}
