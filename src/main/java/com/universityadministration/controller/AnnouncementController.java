package com.universityadministration.controller;


import com.universityadministration.dto.AnnouncementDto;
import com.universityadministration.helper.UserService;
import com.universityadministration.model.Announcement;
import com.universityadministration.model.Notification;
import com.universityadministration.service.AnnouncementService;
import com.universityadministration.service.NotificationService;
import com.universityadministration.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private UserService userService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private ServiceImpl serviceImpl;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/list/{announcementType}")
    public ModelAndView showListOfAnnouncement(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "announcementType")String announcementType){
        ModelAndView mv = new ModelAndView("AnnouncementViews/List");
        mv.addObject("announcements", announcementService.getAnnouncementByType(announcementType));
        mv.addObject("announcementType", announcementType);

        if(announcementType.toLowerCase().contains("general"))
            announcementType = "General";
        mv.addObject("header", "List Of " + announcementType + " Announcements");
        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mv.addObject("notifications",notifications);

        return mv;
    }

    @RequestMapping("/create/{announcementType}")
    public ModelAndView showCreateAnnouncement(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "announcementType")String announcementType){
        ModelAndView mv = new ModelAndView("AnnouncementViews/Create");
        AnnouncementDto announcementDto = new AnnouncementDto();
        announcementDto.setType(announcementType);
        mv.addObject("announcementDto", announcementDto);
        if(announcementType.toLowerCase().contains("general"))
            announcementType = "General";

        mv.addObject("header", "Add " + announcementType + " Announcement");

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mv.addObject("notifications",notifications);

        return mv;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ModelAndView saveAnnouncement(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute("announcementDto") AnnouncementDto announcementDto){
        Announcement announcement = new Announcement();
        announcement.setEmployee(userService.getByEmail(userDetails.getUsername()));
        announcement.setTitle(announcementDto.getTitle());
        announcement.setDescription(announcementDto.getDescription());
        announcement.setType(announcementDto.getType());
        announcement.setStudyLevel(announcementDto.getStudyLevel());
        announcement.setStudyYear(announcementDto.getStudyYear());
        announcement.setPeriod(announcementDto.getPeriod());
        announcement.setMaterialName(announcementDto.getMaterialName());
        announcement.setMarks(serviceImpl.saveImage(announcementDto.getMarks()));

        announcementService.save(announcement);

        int announcementId = announcementService.findAll().get(announcementService.findAll().size() - 1).getId();

        notificationService.publishAnnouncementNotification(announcementId);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        return new ModelAndView("AnnouncementViews/List").addObject("announcements", announcementService.getAnnouncementByType(announcementDto.getType())).addObject("notifications",notifications);
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditAnnouncement(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") int id){
        ModelAndView mv = new ModelAndView("AnnouncementViews/Edit");
        Announcement announcement = announcementService.getById(id);
        AnnouncementDto announcementDto = new AnnouncementDto();
        announcementDto.setId(id);
        announcementDto.setTitle(announcement.getTitle());
        announcementDto.setDescription(announcement.getDescription());
        announcementDto.setType(announcement.getType());
        announcementDto.setPeriod(announcementDto.getPeriod());
        announcementDto.setMaterialName(announcement.getMaterialName());
        announcementDto.setStudyLevel(announcementDto.getStudyLevel());
        announcementDto.setStudyYear(announcement.getStudyYear());

        if(!announcement.getMarks().contains("."))
            announcementDto.setMarksImg(null);
        else
            announcementDto.setMarksImg(announcement.getMarks());

        String announcementType = announcement.getType();
        if(announcement.getType().toLowerCase().contains("general"))
            announcementType = "General";

        mv.addObject("header", "Update " + announcementType + " Announcement");

        mv.addObject("announcementDto", announcementDto);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mv.addObject("notifications",notifications);
        return mv;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ModelAndView updateAnnouncement(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute("announcementDto") AnnouncementDto announcementDto){
        Announcement announcement = announcementService.getById(announcementDto.getId());
        announcement.setTitle(announcementDto.getTitle());
        announcement.setDescription(announcementDto.getDescription());
        announcement.setType(announcementDto.getType());
        announcement.setStudyLevel(announcementDto.getStudyLevel());
        announcement.setStudyYear(announcementDto.getStudyYear());
        announcement.setPeriod(announcementDto.getPeriod());
        announcement.setMaterialName(announcementDto.getMaterialName());
        if(!announcementDto.getMarks().isEmpty())
            announcement.setMarks(serviceImpl.saveImage(announcementDto.getMarks()));

        announcementService.save(announcement);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        return new ModelAndView("AnnouncementViews/List").addObject("announcements", announcementService.getAnnouncementByType(announcementDto.getType())).addObject("notifications",notifications);
    }

    @RequestMapping("/details/{id}")
    public ModelAndView detailsAnnouncement(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") int id){
        ModelAndView mv = new ModelAndView("AnnouncementViews/Details");
        Announcement announcement = announcementService.getById(id);
        AnnouncementDto announcementDto = new AnnouncementDto();
        announcementDto.setId(id);
        announcementDto.setTitle(announcement.getTitle());
        announcementDto.setDescription(announcement.getDescription());
        announcementDto.setType(announcement.getType());
        announcementDto.setPeriod(announcement.getPeriod());
        announcementDto.setMaterialName(announcement.getMaterialName());
        announcementDto.setStudyLevel(announcement.getStudyLevel());
        announcementDto.setStudyYear(announcement.getStudyYear());
        if(!announcement.getMarks().contains("."))
            announcementDto.setMarksImg(null);
        else
            announcementDto.setMarksImg(announcement.getMarks());

        String announcementType = announcement.getType();
        if(announcement.getType().toLowerCase().contains("general"))
            announcementType = "General";

        mv.addObject("header", "Details of " + announcementType + " Announcement");

        mv.addObject("announcementDto", announcementDto);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());
        mv.addObject("notifications", notifications);
        return mv;
    }

    @RequestMapping("/delete/{id}")
    public ModelAndView deleteAnnouncement(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") int id){
        String type = announcementService.getById(id).getType();
        announcementService.deleteAnnouncement(id);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        return new ModelAndView("AnnouncementViews/List").addObject("announcements", announcementService.getAnnouncementByType(type)).addObject("notifications", notifications);
    }

}
