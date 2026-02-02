package com.webapp.demo_app.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettings {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private boolean availabilitySundayOnly = true;
}

