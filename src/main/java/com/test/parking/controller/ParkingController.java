package com.test.parking.controller;


import com.test.parking.bean.Parking;
import com.test.parking.service.ParkingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/parkings/{x}/{y}")
    public List<Parking> getAproximateOrdredParkings(@PathVariable double x,
                                                     @PathVariable double y
                                                     ) throws IOException, URISyntaxException {
        return parkingService.getAproximateParkingList(x,y);
    }
}
