package com.universityadministration.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "notification")
public class Notification implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String decription;

    private String url;

    private String userEmail;

    public Notification(String decription, String url, String userEmail) {
        this.decription = decription;
        this.url = url;
        this.userEmail = userEmail;
    }

    public Notification() {

    }
}
