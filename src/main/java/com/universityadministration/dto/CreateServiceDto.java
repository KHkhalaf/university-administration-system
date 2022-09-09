package com.universityadministration.dto;

import com.universityadministration.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class CreateServiceDto {
    private int id;
    private User student;
    private User employee;
    private String serviceType;
    private String serviceStatus;
    private String materialName;
    private int materialMark;
    private MultipartFile identification;
    private MultipartFile collageCard;
    private MultipartFile graduationCertificate;
    private MultipartFile paymentProve;
    private MultipartFile bloodDonationProve;
    private MultipartFile marksReport;
    private MultipartFile statusReport;
    private MultipartFile studentTranscript;
    private MultipartFile studentClearance;
    private MultipartFile universityHousingClearance;
}
