package com.test.parking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.parking.bean.Field;
import com.test.parking.bean.Geometry;
import com.test.parking.bean.Parking;
import com.test.parking.bean.RuntimeDetail;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONTokener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.test.parking.util.Traitement.distanceEarth;

@Service
public class ParkingService {


    private RuntimeDetail rumTimeDetails(String nomParm, Geometry geometryParam) throws MalformedURLException {

        URL runtimeUrl = new URL("https://data.grandpoitiers.fr/api/records/1.0/search/?dataset=mobilites-stationnement-des-parkings-en-temps-reel&facet=nom");
        RuntimeDetail runtimeDetail = null;
        RuntimeDetail runtimeDetailVar = new RuntimeDetail();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(runtimeUrl.toString(), String.class);
        String json = responseEntity.getBody();


        try {
            JSONObject data = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray pages = data.getJSONArray("records");
            for (int i = 0; i < pages.length(); i++) {
                runtimeDetail = new ObjectMapper().readValue(pages.getJSONObject(i).getJSONObject("fields").toString(), RuntimeDetail.class);
                if (runtimeDetail.getNom().equals(nomParm))
                    runtimeDetailVar = runtimeDetail;
                if (pages.getJSONObject(i).has("geometry") && geometryParam != null) {
                    Geometry geometry = new ObjectMapper().readValue(pages.getJSONObject(i).getJSONObject("geometry").toString(), Geometry.class);
                    if (geometryParam.equals(geometry)) {
                        runtimeDetail.setGeometry(geometry);
                        runtimeDetailVar = runtimeDetail;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return runtimeDetailVar;
    }


    public List<Parking> getAproximateParkingList(double x, double y) throws IOException, URISyntaxException {

        URL url = new URL("https://data.grandpoitiers.fr/api/records/1.0/search/?dataset=mobilite-parkings-grand-poitiers-donnees-metiers&rows=1000&facet=nom_du_parking&facet=zone_tarifaire&facet=statut2&facet=statut3");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url.toString(), String.class);
        String json = responseEntity.getBody();
        List<Parking> parkingList = new ArrayList<>();


        try {
            JSONObject data = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray pages = data.getJSONArray("records");
            for (int i = 0; i < pages.length(); i++) {
                Parking parking = new ObjectMapper().readValue(pages.get(i).toString(), Parking.class);
                parking.setDistance(0);
                Field field = new ObjectMapper().readValue(pages.getJSONObject(i).getJSONObject("fields").toString(), Field.class);
                if(pages.getJSONObject(i).has("geometry")) {
                    Geometry geometry = new ObjectMapper().readValue(pages.getJSONObject(i).getJSONObject("geometry").toString(), Geometry.class);
                    parking.setGeometry(geometry);
                    parking.setDistance(distanceEarth(x, geometry.getCoordinates()[0], y, geometry.getCoordinates()[1], 0, 0));
                }
                parking.setField(field);
                parking.setRuntimeDetail(rumTimeDetails(parking.getField().getNom(), parking.getGeometry()));
                parkingList.add(parking);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(parkingList);
        return  parkingList;
    }




}
