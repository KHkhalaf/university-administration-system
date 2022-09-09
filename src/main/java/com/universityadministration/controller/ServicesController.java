package com.universityadministration.controller;

import com.universityadministration.dto.CreateServiceDto;
import com.universityadministration.dto.ServiceListDto;
import com.universityadministration.dto.UpdateServiceDto;
import com.universityadministration.exceptions.ResourceNotFoundException;
import com.universityadministration.helper.UserService;
import com.universityadministration.model.Notification;
import com.universityadministration.model.Service;
import com.universityadministration.repository.ServicesRepository;
import com.universityadministration.service.NotificationService;
import com.universityadministration.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/services")
public class ServicesController {
    @Autowired
    private UserService userService;
    @Autowired
    private ServicesRepository serviceRepository;

    @Autowired
    private ServiceImpl serviceImpl;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/list/{serviceType}")
    public ModelAndView showListServicePage(@PathVariable(name = "serviceType") String serviceType, @AuthenticationPrincipal UserDetails userDetails) {
        ModelAndView mv = new ModelAndView("ServicesViews/List");
        mv.addObject("serviceType", serviceType);
        List<ServiceListDto> services = getServices(userDetails.getUsername(), serviceType);

        mv.addObject("serviceType", serviceType);
        mv.addObject("services", services);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());
        mv.addObject("notifications", notifications);
        return mv;
    }

    @RequestMapping(value = "/create/{serviceType}")
    public ModelAndView showCreatePage(@AuthenticationPrincipal UserDetails userDetails, CreateServiceDto createServiceDto, @PathVariable(name = "serviceType") String serviceType) {

        ModelAndView mav = new ModelAndView("ServicesViews/create");
        createServiceDto.setServiceType(serviceType);
        mav.addObject("createServiceDto", createServiceDto);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());
        mav.addObject("notifications", notifications);
        return mav;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ModelAndView save(@AuthenticationPrincipal UserDetails currentUser, @ModelAttribute("createServiceDto") CreateServiceDto createServiceDto) throws IOException {
        Service service = new Service();
        service.setStudent(userService.getByEmail(currentUser.getUsername()));
        service.setServiceType(createServiceDto.getServiceType());
        service.setDateOfApplication(LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd")));
        service.setServiceStatus("REQUESTED");
        service.setMaterialName(createServiceDto.getMaterialName());

        service.setIdentification(serviceImpl.saveImage(createServiceDto.getIdentification()));
        service.setCollageCard(serviceImpl.saveImage(createServiceDto.getCollageCard()));
        service.setGraduationCertificate(serviceImpl.saveImage(createServiceDto.getGraduationCertificate()));
        service.setPaymentProve(serviceImpl.saveImage(createServiceDto.getPaymentProve()));
        service.setBloodDonationProve(serviceImpl.saveImage(createServiceDto.getBloodDonationProve()));
        service.setStudentClearance(serviceImpl.saveImage(createServiceDto.getStudentClearance()));
        service.setUniversityHousingClearance(serviceImpl.saveImage(createServiceDto.getUniversityHousingClearance()));

        serviceRepository.save(service);
        int serviceId = serviceRepository.findAll().get(serviceRepository.findAll().size() - 1).getId();

        notificationService.publishRequestedService(serviceId);

        List<ServiceListDto> services = getServices(currentUser.getUsername(), createServiceDto.getServiceType());

        List<Notification> notifications = notificationService.getNotificationByEmail(currentUser.getUsername());

        return new ModelAndView("ServicesViews/List").addObject("services", services).addObject("notifications", notifications);
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditServicePage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("ServicesViews/Update");
        Service service = serviceRepository.findById(id).get();
        UpdateServiceDto updateServiceDto = new UpdateServiceDto();
        updateServiceDto.toService(service);
        mav.addObject("updatedServiceDto", updateServiceDto);

        return mav;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ModelAndView updateServicePage(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute("updatedServiceDto") UpdateServiceDto updateServiceDto) {

        Service service = serviceRepository.findById(updateServiceDto.getId()).get();
        service.setMaterialName(updateServiceDto.getMaterialName());

        if (updateServiceDto.getIdentification() != null && !updateServiceDto.getIdentification().isEmpty())
            service.setIdentification(serviceImpl.saveImage(updateServiceDto.getIdentification()));
        if (updateServiceDto.getCollageCard() != null && !updateServiceDto.getCollageCard().isEmpty())
            service.setCollageCard(serviceImpl.saveImage(updateServiceDto.getCollageCard()));
        if (updateServiceDto.getGraduationCertificate() != null && !updateServiceDto.getGraduationCertificate().isEmpty())
            service.setGraduationCertificate(serviceImpl.saveImage(updateServiceDto.getGraduationCertificate()));
        if (updateServiceDto.getPaymentProve() != null && !updateServiceDto.getPaymentProve().isEmpty())
            service.setPaymentProve(serviceImpl.saveImage(updateServiceDto.getPaymentProve()));
        if (updateServiceDto.getBloodDonationProve() != null && !updateServiceDto.getBloodDonationProve().isEmpty())
            service.setBloodDonationProve(serviceImpl.saveImage(updateServiceDto.getBloodDonationProve()));
        if (updateServiceDto.getStudentClearance() != null && !updateServiceDto.getStudentClearance().isEmpty())
            service.setStudentClearance(serviceImpl.saveImage(updateServiceDto.getStudentClearance()));
        if (updateServiceDto.getUniversityHousingClearance() != null && !updateServiceDto.getUniversityHousingClearance().isEmpty())
            service.setUniversityHousingClearance(serviceImpl.saveImage(updateServiceDto.getUniversityHousingClearance()));

        serviceRepository.save(service);

        List<ServiceListDto> services = getServices(userDetails.getUsername(), updateServiceDto.getServiceType());
        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        return new ModelAndView("ServicesViews/List").addObject("services", services).addObject("notifications", notifications);
    }

    @RequestMapping("/details/{id}")
    public ModelAndView showDetailsService(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("ServicesViews/Details");
        Service service = serviceRepository.findById(id).get();
        UpdateServiceDto updateServiceDto = new UpdateServiceDto();
        updateServiceDto.toService(service);
        mav.addObject("updatedServiceDto", updateServiceDto);
        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mav.addObject("notifications", notifications);

        return mav;
    }

    @RequestMapping("/delete/{id}")
    public ModelAndView deleteService(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") int id) {
        String type = serviceRepository.getById(id).getServiceType();
        serviceRepository.deleteById(id);


        List<ServiceListDto> services = getServices(userDetails.getUsername(), type);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        return new ModelAndView("ServicesViews/List").addObject("services", services).addObject("notifications", notifications);
    }

    @RequestMapping("/approve/{id}")
    public ModelAndView showApproveServicePage(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("ServicesViews/Approve");
        Service service = serviceRepository.findById(id).get();
        UpdateServiceDto updateServiceDto = new UpdateServiceDto();
        updateServiceDto.toService(service);
        mav.addObject("updatedServiceDto", updateServiceDto);

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        mav.addObject("notifications", notifications);

        return mav;
    }

    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    public ModelAndView approveService(@ModelAttribute("updatedServiceDto") UpdateServiceDto updateServiceDto, @AuthenticationPrincipal UserDetails userDetails) {
        Service service = serviceRepository.getById(updateServiceDto.getId());
        if (updateServiceDto.getStudentTranscript() != null && !updateServiceDto.getStudentTranscript().isEmpty())
            service.setCancelRegistration(serviceImpl.saveImage(updateServiceDto.getStudentTranscript()));
        if (updateServiceDto.getStatusReport() != null && !updateServiceDto.getStatusReport().isEmpty())
            service.setStatusReport(serviceImpl.saveImage(updateServiceDto.getStatusReport()));
        if (updateServiceDto.getDocumentForDeferment() != null && !updateServiceDto.getDocumentForDeferment().isEmpty())
            service.setMarksReport(serviceImpl.saveImage(updateServiceDto.getDocumentForDeferment()));
        if (updateServiceDto.getGraduationCertificate() != null && !updateServiceDto.getGraduationCertificate().isEmpty())
            service.setGraduationCertificate(serviceImpl.saveImage(updateServiceDto.getGraduationCertificate()));

        service.setMaterialMark(updateServiceDto.getMaterialMark());
        String serviceStatus = service.getServiceStatus();
        if (service.getServiceStatus().equals("REQUESTED")){//if(Employeee)
            service.setServiceStatus("APPROVED BY EMPLOYEE");
        }
        else if(service.getServiceStatus().equals("APPROVED BY EMPLOYEE")){//if(Dean)
            service.setServiceStatus("APPROVED BY DEAN");
        }

        service.setEmployee(userService.getByEmail(userDetails.getUsername()));

        serviceRepository.save(service);

        int serviceId = serviceRepository.findAll().get(serviceRepository.findAll().size() - 1).getId();

        if (serviceStatus.equals("REQUESTED"))
            notificationService.publishServiceApprovedByEmployee(serviceId);
        else if(serviceStatus.equals("APPROVED BY EMPLOYEE"))
            notificationService.publishServiceApprovedByDean(serviceId);


        List<ServiceListDto> services = getServices(userDetails.getUsername(), updateServiceDto.getServiceType());

        List<Notification> notifications = notificationService.getNotificationByEmail(userDetails.getUsername());

        return new ModelAndView("ServicesViews/List").addObject("services", services).addObject("notifications", notifications);
    }

    @RequestMapping(path = "/download/{fileName}", method = RequestMethod.GET)
    public ResponseEntity<?> download( @PathVariable(name = "fileName") String fileName) throws IOException {
        return serviceImpl.download(fileName);
    }


    private List<ServiceListDto> getServices(String email, String serviceType){
        List<ServiceListDto> services;
        if (userService.getByEmail(email).getType().equals("STUDENT")) {
            services = serviceImpl.getServicesForStudentByType(email, serviceType);
        } else if (userService.getByEmail(email).getType().equals("DEAN")) {
            services = serviceImpl.getServicesForDeanByType(serviceType);
        } else
            services = serviceImpl.getServicesByType(serviceType);

        return services;
    }
}
