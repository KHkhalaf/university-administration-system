package com.universityadministration.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class AnnouncementDto {
    private int id;

    private String title;

    private String description;

    private String type;

    private String studyLevel;

    private String studyYear;

    private String period;

    private String materialName;

    private MultipartFile marks;

    private String marksImg;

    public AnnouncementDto() {
    }

    public AnnouncementDto(int id, String title, String description, String type, String studyLevel, String studyYear, String period, String materialName, String marksImg) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.studyLevel = studyLevel;
        this.studyYear = studyYear;
        this.period = period;
        this.materialName = materialName;
        this.marks = marks;
        this.marksImg = marksImg;
    }
}
