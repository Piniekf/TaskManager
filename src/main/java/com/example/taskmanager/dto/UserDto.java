//Klasa UserDto jest używana do przekazywania danych użytkownika między warstwą prezentacji
// tj. formularz logowania i rejestracji, a kontrolerem w aplikacji.

package com.example.taskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{
    private Long id;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty(message = "Email nie może być pusty")
    @Email
    private String email;
    @NotEmpty(message = "Hasło nie może być puste")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$", message = "Hasło musi mieć co najmniej 8 znaków, małą i wielką literę, cyfrę oraz znak specjalny.")
    private String password;

    public Boolean isActivated = false;
    public Date isActivatedExpiryDate;
    public String isActivatedToken;
}
