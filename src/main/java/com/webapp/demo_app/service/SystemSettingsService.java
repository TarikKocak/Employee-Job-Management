package com.webapp.demo_app.service;

import com.webapp.demo_app.repository.SystemSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.webapp.demo_app.model.SystemSettings;

@Service
@Transactional
public class SystemSettingsService {

    private final SystemSettingsRepository repository;

    public SystemSettingsService(SystemSettingsRepository repository) {
        this.repository = repository;
    }

    public boolean isAvailabilitySundayOnlyEnabled() {
        return repository.findById(1L)
                .map(SystemSettings::isAvailabilitySundayOnly)
                .orElse(true); // fail-safe
    }

    public void updateAvailabilitySundayOnly(boolean enabled) {
        SystemSettings settings = repository.findById(1L)
                .orElseGet(() -> {
                    SystemSettings s = new SystemSettings();
                    s.setId(1L);
                    return s;
                });

        settings.setAvailabilitySundayOnly(enabled);
        repository.save(settings);
    }
}
