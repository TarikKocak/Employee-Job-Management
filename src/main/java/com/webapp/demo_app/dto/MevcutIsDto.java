package com.webapp.demo_app.dto;

import com.webapp.demo_app.model.enums.Tur;
import com.webapp.demo_app.model.enums.UcretTahsilTipi;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MevcutIsDto {

    private Long id;

    // Readable fields for employee UI
    private Tur tur;
    private Boolean duvarMontaji;
    private String isim;
    private String isAdresi;
    private String telNo;
    private String isTanimi;

    private LocalDate tarih;
    private LocalTime baslangicSaati;

    private Double tahminiSure;

    // keep payment type visible if you want the employee to understand why "No Info"
    private UcretTahsilTipi ucretTahsilTipi;

    // price for UI (controlled)
    private String ucret; // "120€" or "No Info"
}
