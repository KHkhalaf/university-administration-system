package com.universityadministration.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "service")
public class Service implements Serializable {
    @Id
    @Column(name="service_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @ManyToOne
    @JoinColumn(name="student_id", nullable=false)
    private User student;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private User employee;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "date_of_application")
    private String dateOfApplication;

    @Column(name = "service_status")
    private String serviceStatus;

    @Column(name="identification")
    private String identification;

    @Column(name = "collage_card")
    private String collageCard;

    @Column(name = "graduation_certificate")
    private String graduationCertificate;

    @Column(name = "payment_prove")
    private String paymentProve;

    @Column(name = "blood_donation_prove")
    private String bloodDonationProve;

    @Column(name = "marks_report")
    private String marksReport;

    @Column(name = "status_report")
    private String statusReport;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "material_mark")
    private int materialMark ;

    @Column(name = "cancel_registration")
    private String cancelRegistration;

    @Column(name = "student_clearance")
    private String studentClearance;

    @Column(name = "university_housing_clearance")
    private String universityHousingClearance;


}
