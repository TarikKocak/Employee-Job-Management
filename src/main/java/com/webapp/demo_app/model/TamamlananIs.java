package com.webapp.demo_app.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tamamlanan_isler")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TamamlananIs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // İş ID (readonly )

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private Tur tur;

    private Boolean duvarMontaji;

    private String isAdresi;
    private String tanimi;
    private LocalDate tarih;
    private Double ucret;

    @Enumerated(EnumType.STRING)
    private UcretTahsilTipi ucretTahsilTipi;

    private Double sure;
    private Integer bahsis;
    private Boolean kartVerildi;
    private Boolean yorumKartiVerildi;
    private Boolean fotoAtildi;
}
