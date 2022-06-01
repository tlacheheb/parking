package com.test.parking.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class RuntimeDetail {

    private String nom;
    private int taux_doccupation;
    private int capacite;
    private int places_restantes;
    private Date record_timestamp;
    private Geometry geometry;
}
