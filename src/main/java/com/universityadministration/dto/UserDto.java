package com.universityadministration.dto;

import com.universityadministration.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class UserDto {
    private int id;
    private String username;
    private String password;
    private String email;
    private String address;
    private String type;
    private String specialization;
    private String phoneNumber;
    private String nationality;
    private String dateOfRegistration;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String birthDay;
    private String placeOfBirth;
    private String gender;
    private MultipartFile highSchoolCertificate;
    private String imageName;

    public void toUserDto(User user){
        id= user.getId();
        username = user.getUsername();
        email = user.getEmail();
        type = user.getType();
        address = user.getAddress();
        specialization = user.getSpecialization();
        phoneNumber = user.getPhoneNumber();
        nationality = user.getNationality();
        dateOfRegistration = user.getDateOfRegistration();
        birthDay = user.getPlaceOfBirth();
        placeOfBirth = user.getPlaceOfBirth();
        gender = user.getGender();
        imageName = user.getHighSchoolCertificate();
    }
}
