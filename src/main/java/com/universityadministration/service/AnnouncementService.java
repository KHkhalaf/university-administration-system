package com.universityadministration.service;

import com.universityadministration.dto.AnnouncementDto;
import com.universityadministration.model.Announcement;
import com.universityadministration.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;

    public List<AnnouncementDto> getAnnouncementByType(String announcementType){
        List<AnnouncementDto> announcements = new ArrayList<>();
        announcementRepository.findAll().forEach(a->{
            if(a.getType().equals(announcementType))
                announcements.add(new AnnouncementDto(a.getId(), a.getTitle(), a.getDescription(), a.getType(), a.getStudyLevel(), a.getStudyYear(), a.getPeriod(), a.getMaterialName(), a.getMarks()));
        });

        return announcements;
    }
    public List<Announcement> findAll(){
        return announcementRepository.findAll().stream().toList();
    }

    public void save(Announcement announcement){
        announcementRepository.save(announcement);
    }

    public Announcement getById(int id){
        return announcementRepository.findById(id).get();
    }

    public void deleteAnnouncement(int id){
        announcementRepository.deleteById(id);
    }
}
