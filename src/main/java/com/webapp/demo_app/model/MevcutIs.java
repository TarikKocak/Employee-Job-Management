package com.webapp.demo_app.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "mevcut_isler")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MevcutIs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // READONLY
    @Enumerated(EnumType.STRING)
    private Tur tur;

    private Boolean duvarMontaji;
    private String isim;
    private String isAdresi;
    private String telNo;
    private String isTanimi;
    private LocalDate tarih;

    private LocalTime baslangicSaati;
    private Double ucret;

    @Enumerated(EnumType.STRING)
    private UcretTahsilTipi ucretTahsilTipi; // CASH, BANK

    private Double tahminiSure;

    // WRITEONLY  (All null)
    private Double sure;
    private Integer bahsis;
    private Boolean kartVerildi;
    private Boolean yorumKartiVerildi;
    private Boolean fotoAtildi;
}