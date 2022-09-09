package com.universityadministration.model;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "announcement")
public class Announcement implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="employee_id", nullable=false)
    private User employee;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "study_level")
    private String studyLevel;

    @Column(name = "study_year")
    private String studyYear;

    @Column(name = "period")
    private String period;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "marks")
    private String marks;

}
