package com.webapp.demo_app.dto;

import com.webapp.demo_app.model.enums.Tur;
import com.webapp.demo_app.model.enums.UcretTahsilTipi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TamamlananIsDto {

    private Long id;
    private Tur tur;
    private Boolean duvarMontaji;
    private String isAdresi;
    private String tanimi;
    private LocalDate tarih;

    private String ucret; //  STRING ON PURPOSE
    private UcretTahsilTipi ucretTahsilTipi;

    private Double sure;
    private Integer bahsis;
    private Boolean kartVerildi;
    private Boolean yorumKartiVerildi;
    private Boolean fotoAtildi;

    // getters & setters
}
