package com.universityadministration.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.universityadministration.dto.UserDto;
import com.universityadministration.dto.UserSummary;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User implements Serializable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private boolean enabled;

    @Column(length = 50, name = "is_verified")
    private Boolean isVerified;

    private String phoneNumber;
    private String nationality;
    private String birthDay;
    private String dateOfRegistration;
    private String placeOfBirth;
    private String gender;
    private String specialization;
    private String type;
    private String highSchoolCertificate;
    private String address;

    @Column(length = 100, unique = true)
    @NotNull
    @Email
    private String email;

    @OneToMany(mappedBy="student")
    private Set<Service> services;

    @OneToMany(mappedBy="employee")
    private Set<Service> approvedServices;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_Id")})

    private Set<Authority> authorities;

    public Set<Authority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public UserSummary toUserSummary() {
        UserSummary userSummary = new UserSummary();
        userSummary.setEmail(this.email);
        userSummary.setUserId(this.id);
        return userSummary;
    }

    public void toUser(UserDto userDto){
        username = userDto.getUsername();
        email = userDto.getEmail();
        type = userDto.getType();
        if(password == null || password.isEmpty())
            password = new BCryptPasswordEncoder().encode(userDto.getPassword());
        address = userDto.getAddress();
        specialization = userDto.getSpecialization();
        birthDay = userDto.getBirthDay();
        phoneNumber = userDto.getPhoneNumber();
        nationality = userDto.getNationality();
        if(dateOfRegistration == null || dateOfRegistration.isEmpty())
            dateOfRegistration = LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        placeOfBirth = userDto.getPlaceOfBirth();
        gender = userDto.getGender();
    }

}