package com.universityadministration.dto;

import com.universityadministration.model.Service;
import com.universityadministration.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Setter
@Getter
public class UpdateServiceDto {
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
    private MultipartFile documentForDeferment;
    private MultipartFile statusReport;
    private MultipartFile studentTranscript;
    private MultipartFile studentClearance;
    private MultipartFile universityHousingClearance;

    private String identificationImg;
    private String collageCardImg;
    private String graduationCertificateImg;
    private String paymentProveImg;
    private String bloodDonationProveImg;
    private String documentForDefermentImg;
    private String statusReportImg;
    private String studentTranscriptImg;
    private String studentClearanceImg;
    private String universityHousingClearanceImg;
    private String marksReport;
    private String cancelRegistration;

    public void toService(Service service){
        id = service.getId();
        student = service.getStudent();
        employee = service.getEmployee();
        serviceType = service.getServiceType();
        serviceStatus = service.getServiceStatus();
        materialName = service.getMaterialName();
        materialMark = service.getMaterialMark();
        identificationImg = service.getIdentification();
        collageCardImg = service.getCollageCard();
        graduationCertificateImg = service.getGraduationCertificate();
        paymentProveImg = service.getPaymentProve();
        bloodDonationProveImg = service.getBloodDonationProve();
        documentForDefermentImg = service.getMarksReport();
        statusReportImg = service.getStatusReport();
        studentTranscriptImg = service.getCancelRegistration();
        studentClearanceImg = service.getStudentClearance();
        universityHousingClearanceImg = service.getUniversityHousingClearance();
        marksReport = service.getMarksReport();
        cancelRegistration = service.getCancelRegistration();
    }
}
