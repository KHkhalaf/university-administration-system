package com.universityadministration.service;

import com.universityadministration.dto.ServiceListDto;
import com.universityadministration.repository.ServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ServiceImpl{
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\src\\main\\upload\\";

    @Autowired
    private ServicesRepository servicesRepository;

    public List<ServiceListDto> getServicesByType(String type){
        List<ServiceListDto> services = new ArrayList<>();
        servicesRepository.findAll().forEach(s->{
            if(s.getServiceType().equals(type))
                services.add(new ServiceListDto(s.getId(), s.getStudent().getUsername() ,  s.getEmployee() == null ? null : s.getEmployee().getUsername(), s.getServiceType(), s.getServiceStatus()));
        });
        return services;
    }
    public String saveImage(MultipartFile img){
        if(img != null){
            String fileName = System.currentTimeMillis() + "_" + img.getOriginalFilename();
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, img.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileName;
        }
        return "";
    }

    public List<ServiceListDto> getServicesForStudentByType(String email, String type){
        List<ServiceListDto> services = new ArrayList<>();
        servicesRepository.findAll().forEach(s->{
            if(s.getStudent().getEmail().equals(email) && s.getServiceType().equals(type))
                services.add(new ServiceListDto(s.getId(), s.getStudent().getUsername() ,  s.getEmployee() == null ? null : s.getEmployee().getUsername(), s.getServiceType(), s.getServiceStatus()));
        });
        return services;
    }
    public List<ServiceListDto> getServicesForDeanByType(String type){
        List<ServiceListDto> services = new ArrayList<>();
        servicesRepository.findAll().forEach(s->{
            if(!s.getServiceStatus().equals("REQUESTED") && s.getServiceType().equals(type))
                services.add(new ServiceListDto(s.getId(), s.getStudent().getUsername() ,  s.getEmployee() == null ? null : s.getEmployee().getUsername(), s.getServiceType(), s.getServiceStatus()));
        });
        return services;
    }

    public ResponseEntity<?> download(String fileName) {
        Resource resource;
        try {
            String filePath = UPLOAD_DIRECTORY + fileName;
            Path foundFile = Paths.get(filePath);
            resource = new UrlResource(foundFile.toUri());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}
