package com.universityadministration.repository;

import com.universityadministration.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepository extends JpaRepository<Service, Integer> {
}
