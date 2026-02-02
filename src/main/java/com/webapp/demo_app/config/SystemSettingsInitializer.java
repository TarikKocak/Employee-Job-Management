package com.webapp.demo_app.config;

import com.webapp.demo_app.model.SystemSettings;
import com.webapp.demo_app.repository.SystemSettingsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SystemSettingsInitializer {

    private final SystemSettingsRepository repository;

    public SystemSettingsInitializer(SystemSettingsRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        if (!repository.existsById(1L)) {
            SystemSettings settings = new SystemSettings();
            settings.setId(1L);
            settings.setAvailabilitySundayOnly(true); // default

            repository.save(settings);
        }
    }
}
