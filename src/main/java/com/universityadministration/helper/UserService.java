package com.universityadministration.helper;

import com.universityadministration.dto.LoginResponse;
import com.universityadministration.dto.UserSummary;
import com.universityadministration.exceptions.ResourceNotFoundException;
import com.universityadministration.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService {

    ResponseEntity<LoginResponse> login(String email, String accessToken, String refreshToken);

    ResponseEntity<LoginResponse> refresh(String accessToken, String refreshToken);

    UserSummary getUserProfile();

    public List<User> listAll();

    public List<User> listAllByType(String type);

    public void save(User user);

    public User getByEmail(String email);

    public User get(int id) throws ResourceNotFoundException;

    public void delete(int id);

}