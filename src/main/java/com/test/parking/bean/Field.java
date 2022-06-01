package com.test.parking.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class Field {

    private String nom;
    private String adresse;
    private String info;
    private String    nb_places;
    private String    tarif_24h;
    private String    tarif_4h;
    private String    tarif_2h;
    private String    tarif_3h;
    private String    tarif_1h;

}
