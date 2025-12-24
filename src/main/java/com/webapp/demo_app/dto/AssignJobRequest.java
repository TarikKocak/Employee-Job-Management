package com.webapp.demo_app.dto;

import com.webapp.demo_app.model.enums.Tur;
import com.webapp.demo_app.model.enums.UcretTahsilTipi;

import java.time.LocalDate;
import java.time.LocalTime;

public class AssignJobRequest {
    private Tur tur;
    private Boolean duvarMontaji;
    private String isim;
    private String isAdresi;
    private String telNo;
    private String isTanimi;
    private LocalDate tarih;
    private LocalTime baslangicSaati;
    private Double tahminiSure;
    private Double ucret;
    private UcretTahsilTipi ucretTahsilTipi;
}
