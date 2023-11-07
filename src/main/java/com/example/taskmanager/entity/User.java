package com.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}
    )
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    private String resetPasswordToken;
    private Date resetPasswordTokenExpiryDate;
    public Boolean isActivated = false;
    public Date isActivatedExpiryDate;

    public User(String name, String email, String password) { // To jest napisane dla tokena, trzeba by było to chyba potem poprawić bo jest bez sensu w sumie ale działa
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public boolean isResetPasswordTokenValid() {
        return resetPasswordTokenExpiryDate != null && resetPasswordTokenExpiryDate.after(Calendar.getInstance().getTime()); // To jest do resetowania hasłą
    }
    public boolean isActivatedTokenValid() {
        return isActivatedExpiryDate != null && isActivatedExpiryDate.after(Calendar.getInstance().getTime()); // To jest do weryfikacji email
    }
}
