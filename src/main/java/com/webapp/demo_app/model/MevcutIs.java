package com.webapp.demo_app.model;


import com.webapp.demo_app.model.enums.Tur;
import com.webapp.demo_app.model.enums.UcretTahsilTipi;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;


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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate tarih;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
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