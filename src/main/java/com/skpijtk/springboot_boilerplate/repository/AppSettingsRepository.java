package com.skpijtk.springboot_boilerplate.repository;

import com.skpijtk.springboot_boilerplate.model.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSettingsRepository extends JpaRepository<AppSettings, Integer> {
    
}