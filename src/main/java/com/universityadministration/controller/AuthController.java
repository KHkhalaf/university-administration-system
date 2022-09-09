package com.universityadministration.controller;


import com.universityadministration.dto.LoginRequest;
import com.universityadministration.dto.LoginResponse;
import com.universityadministration.dto.UserDto;
import com.universityadministration.helper.UserService;
import com.universityadministration.model.ConfirmationToken;
import com.universityadministration.model.User;
import com.universityadministration.repository.AuthorityRepository;
import com.universityadministration.repository.ConfirmationTokenRepository;
import com.universityadministration.service.EmailSenderService;
import com.universityadministration.service.InjectSqlCommand;
import com.universityadministration.util.SecurityCipher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/auth")
public class AuthController {

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\src\\main\\upload\\";
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @RequestMapping(value = "/register")
    public ModelAndView showRegisterPage(UserDto userDto) {
        ModelAndView mav = new ModelAndView("UserViews/AddUser");
        mav.addObject("userDto",userDto);
        return mav;
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public ModelAndView Register(@ModelAttribute("userDto") UserDto userDto) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        String fileName = System.currentTimeMillis() + "_" + userDto.getHighSchoolCertificate().getOriginalFilename();
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
        try {
            if(!userDto.getHighSchoolCertificate().getOriginalFilename().isEmpty())
                Files.write(fileNameAndPath, userDto.getHighSchoolCertificate().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setHighSchoolCertificate(fileName);
        user.toUser(userDto);

        User _user = userService.getByEmail(user.getEmail());
        if(_user != null)
        {
            modelAndView.addObject("message","This email already exists!");
            modelAndView.setViewName("Shared/error");
        }
        else
        {
            userService.save(user);
            long userId = userService.getByEmail(user.getEmail()).getId();
            InjectSqlCommand injectSqlCommand = new InjectSqlCommand();
            injectSqlCommand.addUserToAuthority(userId, user.getType());

            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            EmailSenderService emailSenderService = new EmailSenderService();
            emailSenderService.sendEmail(confirmationToken.getConfirmationToken(), user.getEmail());

            confirmationTokenRepository.save(confirmationToken);

            modelAndView.addObject("emailId", user.getEmail());

            modelAndView.setViewName("Shared/successfulRegistration");
        }
        return modelAndView;
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            User user = userService.getByEmail(token.getUser().getEmail());
            user.setIsVerified(true);
            userService.save(user);
            modelAndView.setViewName("Shared/accountVerified");
        }
        else
        {
            modelAndView.addObject("message","The link is invalid or broken!");
            modelAndView.setViewName("Shared/error");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/loginPage")
    public ModelAndView showLoginPage(LoginRequest loginRequest) {
        ModelAndView mav = new ModelAndView("UserViews/login");
        mav.addObject("loginRequest",loginRequest);
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody LoginRequest loginRequest
    ) throws IOException {

        if(!userService.getByEmail(loginRequest.getEmail()).getIsVerified())
            return null;
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);

        return userService.login(loginRequest.getEmail(), decryptedAccessToken, decryptedRefreshToken);
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = "accessToken", required = false) String accessToken,
                                                      @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);
        return userService.refresh(decryptedAccessToken, decryptedRefreshToken);
    }

    @RequestMapping(value = "/logout")
    public String logout( @RequestParam("token")String confirmationToken) throws ParseException {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        token.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss").parse("2020-08-05 17:06:05"));
        confirmationTokenRepository.save(token);

        return "redirect:/auth/loginPage";
    }
    @RequestMapping(value = "/accountNotVerified")
    public ModelAndView accountNotVerified(){

        ModelAndView mav = new ModelAndView("Shared/accountNotVerified");
        return mav;
    }
}