package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserDto; // Import UserDto
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.RoleRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private JavaMailSender javaMailSender;
    private static final int TOKEN_EXPIRATION_MINUTES = 10; // Ważność tokenu
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) { // Aktualizacja przyjmowania obiektu UserDto
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        String activationToken = generateActiveToken();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, TOKEN_EXPIRATION_MINUTES);
        Date tokenExpiryDate = calendar.getTime();
        user.setIsActivatedToken(activationToken);
        user.setIsActivatedExpiryDate(tokenExpiryDate);
        sendActivationEmail(user.getEmail(), user.isActivatedToken);


        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = createDefaultUserRole();
        }

        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User findUserByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    public User findUserByActivationToken(String token) {
        return userRepository.findByIsActivatedToken(token); // Ogarnięte
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private Role createDefaultUserRole() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    // To jest od weryfikacji email

    @Override
    public boolean isActivationTokenValid(String token) {
        User user = userRepository.findByIsActivatedToken(token);
        if (user != null && user.isActivatedTokenValid()) {
            return true;
        }
        return false;
    }

    private String generateActiveToken() {
        return UUID.randomUUID().toString();
    }

    private void sendActivationEmail(String email, String activationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("taskmanagerautomat@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("Aktywacja konta w TaskManager");
        mailMessage.setText("Kliknij w link, aby aktywować swoje konto: http://localhost:8080/activate?token=" + activationToken);
        javaMailSender.send(mailMessage);
    }
}
