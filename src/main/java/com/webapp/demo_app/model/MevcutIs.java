package com.webapp.demo_app.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "mevcut_isler")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MevcutIs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi employee'ye ait
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // READONLY alanlar
    @Enumerated(EnumType.STRING)
    private Tur tur;                   // BERABER / YALNIZ

    private Boolean duvarMontaji;      // true: evet, false: hayır

    private String isim;               // İşin adı / müşteri ismi
    private String isAdresi;
    private String telNo;              // varsa Tel
    private String isTanimi;
    private LocalDate tarih;

    private Double ucret;

    @Enumerated(EnumType.STRING)
    private UcretTahsilTipi ucretTahsilTipi; // CASH, BANK

    private Double tahminiSure;        // tahmini süre (saat)

    // WRITEONLY alanlar (başta null)
    private Double sure;               // gerçek süre (saat)
    private Integer bahsis;
    private Boolean kartVerildi;
    private Boolean yorumKartiVerildi;
    private Boolean fotoAtildi;
}