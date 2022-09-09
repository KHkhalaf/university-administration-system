package com.universityadministration.service;

import com.universityadministration.model.Notification;
import com.universityadministration.model.User;
import com.universityadministration.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserServiceImpl userService;

    public List<Notification> getNotificationByEmail(String email){
        List<Notification> notifications = new ArrayList<>();
        for(Notification notification : notificationRepository.findAll()){
            if(notification.getUserEmail().equals(email))
                notifications.add(notification);
        }
        Collections.reverse(notifications);
        return notifications;
    }

    public void publishAnnouncementNotification(int announcementId){
        List<User> users =userService.listAllByType("STUDENT");

        for (User user : users) {
            Notification notification = new Notification("announcement " + announcementId + " has been posted", "/announcement/details/" + announcementId, user.getEmail());
            notificationRepository.save(notification);
        }
    }

    public void publishRequestedService(int serviceId){
        List<User> users =userService.listAllByType("EMPLOYEE");
        saveServiceNotification(users, serviceId,"REQUESTED", "approve/");
    }

    public void publishServiceApprovedByEmployee(int serviceId){
        List<User> users =userService.listAllByType("DEAN");
        saveServiceNotification(users, serviceId, "APPROVED BY EMPLOYEE", "approve/");
        users =userService.listAllByType("STUDENT");
        saveServiceNotification(users, serviceId, "APPROVED BY EMPLOYEE", "details/");
    }
    public void publishServiceApprovedByDean(int serviceId){
        List<User> users =userService.listAllByType("STUDENT");
        saveServiceNotification(users, serviceId,"APPROVED BY DEAN", "details/");
    }
    private void saveServiceNotification(List<User> users,int serviceId,String serviceStatus, String action){
        for (User user : users) {
            Notification notification = new Notification("service " + serviceId + " has been " + serviceStatus, "/services/" + action + serviceId, user.getEmail());
            notificationRepository.save(notification);
        }
    }
}
