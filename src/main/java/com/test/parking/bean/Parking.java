package com.test.parking.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
//@Document
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class Parking implements Comparable<Parking>{

//    @Id
//    private String id;
    private String datasetid;
    private Field field;
//    @Indexed(unique = true)
    private Geometry geometry;
    private RuntimeDetail runtimeDetail;
    private Date record_timestamp;
    private double distance;



    @Override
    public int compareTo(Parking p) {

            return (int) (getDistance() - p.getDistance());

    }
}































