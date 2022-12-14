package com.universityadministration.service;


import com.universityadministration.dto.*;
import com.universityadministration.exceptions.ResourceNotFoundException;
import com.universityadministration.helper.*;
import com.universityadministration.model.ConfirmationToken;
import com.universityadministration.model.User;
import com.universityadministration.repository.AuthorityRepository;
import com.universityadministration.repository.ConfirmationTokenRepository;
import com.universityadministration.repository.UserRepository;
import com.universityadministration.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public ResponseEntity<LoginResponse> login(String email, String accessToken, String refreshToken) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found with email " + email));

        Boolean accessTokenValid = tokenProvider.validateToken(accessToken);
        Boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);

        HttpHeaders responseHeaders = new HttpHeaders();
        Token newAccessToken;
        Token newRefreshToken;
        if (!accessTokenValid && !refreshTokenValid) {
            newAccessToken = tokenProvider.generateAccessToken(user.getEmail());
            newRefreshToken = tokenProvider.generateRefreshToken(user.getEmail());
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        if (!accessTokenValid && refreshTokenValid) {
            newAccessToken = tokenProvider.generateAccessToken(user.getEmail());
            addAccessTokenCookie(responseHeaders, newAccessToken);
        }

        if (accessTokenValid && refreshTokenValid) {
            newAccessToken = tokenProvider.generateAccessToken(user.getEmail());
            newRefreshToken = tokenProvider.generateRefreshToken(user.getEmail());
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        LoginResponse loginResponse = new LoginResponse(LoginResponse.SuccessFailure.SUCCESS, "Auth successful. Tokens are created in cookie.");

        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);

    }

    @Override
    public ResponseEntity<LoginResponse> refresh(String accessToken, String refreshToken) {
        boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);
        if (!refreshTokenValid) {
            throw new IllegalArgumentException("Refresh Token is invalid!");
        }

        String currentUserEmail = tokenProvider.getUsernameFromToken(accessToken);

        Token newAccessToken = tokenProvider.generateAccessToken(currentUserEmail);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(newAccessToken.getTokenValue(), newAccessToken.getDuration()).toString());

        LoginResponse loginResponse = new LoginResponse(LoginResponse.SuccessFailure.SUCCESS, "Auth successful. Tokens are created in cookie.");
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }

    @Override
    public UserSummary getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(customUserDetails.getUsername()).orElseThrow(() -> new IllegalArgumentException("User not found with email " + customUserDetails.getUsername()));
        return user.toUserSummary();
    }

    @Override
    public List<User> listAll(){
        return userRepository.findAll();
    }

    @Override
    public List<User> listAllByType(String type){
        List<User> users = listAll();
        List<User> _users = new ArrayList<>();
        for(int i=0; i < users.size();i++){
            User user = users.get(i);
            if(type.contains(user.getType()))
                _users.add(user);
        }
        return _users;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User get(int id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + id));
    }

    @Override
    public User getByEmail(String email){
        List<User> users = listAll();
        for(int i=0; i < users.size();i++){
            User user = users.get(i);
            if(user.getEmail().equals(email))
                return user;
        }
        return null;
    }

    @Override
    public void delete(int id) {
        Iterable<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findAll();
        confirmationTokens.forEach( c-> {
            if(c.getUser().getId() == id)
                confirmationTokenRepository.delete(c);
        });
        userRepository.deleteById(id);
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }

    private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }
}