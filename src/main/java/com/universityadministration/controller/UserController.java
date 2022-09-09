package com.universityadministration.controller;

import com.universityadministration.dto.UserDto;
import com.universityadministration.exceptions.ResourceNotFoundException;
import com.universityadministration.helper.UserService;
import com.universityadministration.model.Notification;
import com.universityadministration.model.User;
import com.universityadministration.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/home")
    public ModelAndView showHomePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ModelAndView modelAndView = new ModelAndView("Shared/home");
        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        return new ModelAndView("Shared/home").addObject("notifications",notifications);
    }

    @RequestMapping("/list")
    public ModelAndView showListOfUsers(@AuthenticationPrincipal UserDetails userDetails) {
        List<User> users = userService.listAll();
        ModelAndView mv = new ModelAndView("UserViews/List");
        mv.addObject("users", users);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mv.addObject("notifications", notifications);
        return mv;
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditUserPage(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") int id) throws ResourceNotFoundException {
        ModelAndView mav = new ModelAndView("UserViews/EditUser");
        User user = userService.get(id);
        UserDto userDto = new UserDto();
        userDto.toUserDto(user);

        mav.addObject("userDto", userDto);
        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mav.addObject("notifications", notifications);
        return mav;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ModelAndView saveUser(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute("userDto") UserDto userDto) throws ResourceNotFoundException {
        ModelAndView mav = new ModelAndView("UserViews/profile");
        User user = userService.get(userDto.getId());
        user.toUser(userDto);
        userService.save(user);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mav.addObject("userDto", userDto);
        mav.addObject("notifications", notifications);
        return mav;
    }

    @RequestMapping("/delete/{id}")
    public ModelAndView deleteUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") int id) {
        userService.delete(id);
        List<User> users = userService.listAll();
        ModelAndView mv = new ModelAndView("UserViews/List");
        mv.addObject("users", users);
        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mv.addObject("notifications", notifications);
        return mv;
    }

    @RequestMapping("/details/{id}")
    public ModelAndView profileUser(@PathVariable(name = "id") int id, @AuthenticationPrincipal UserDetails currentUser) throws ResourceNotFoundException {
        ModelAndView modelAndView = new ModelAndView("UserViews/profile");
        User user = null;
        if(id == -1)
            user = userService.getByEmail(currentUser.getUsername());
        else
            user = userService.get(id);
        UserDto userDto = new UserDto();
        userDto.toUserDto(user);
        modelAndView.addObject("userDto", userDto);

        List<Notification> notifications = notificationService.getNotificationByEmail(currentUser.getUsername());

        modelAndView.addObject("notifications", notifications);
        return modelAndView;
    }
}
