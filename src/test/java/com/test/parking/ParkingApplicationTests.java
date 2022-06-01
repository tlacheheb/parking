package com.test.parking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.parking.bean.Field;
import com.test.parking.bean.Geometry;
import com.test.parking.bean.Parking;
import com.test.parking.bean.RuntimeDetail;
import org.junit.jupiter.api.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONTokener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.test.parking.util.Traitement.distanceEarth;


@SpringBootTest
class ParkingApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void restTemplate() throws IOException, URISyntaxException {

        URL url = new URL("https://data.grandpoitiers.fr/api/records/1.0/search/?dataset=mobilite-parkings-grand-poitiers-donnees-metiers&rows=1000&facet=nom_du_parking&facet=zone_tarifaire&facet=statut2&facet=statut3");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url.toString(), String.class);
        String json = responseEntity.getBody();


        try {
            JSONObject data = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray pages = data.getJSONArray("records");
            List<Parking> parkingList = new ArrayList<>();
            for (int i = 0; i < pages.length(); i++) {
                Parking parking = new ObjectMapper().readValue(pages.get(i).toString(), Parking.class);
                Field field = new ObjectMapper().readValue(pages.getJSONObject(i).getJSONObject("fields").toString(), Field.class);
                if(pages.getJSONObject(i).has("geometry")) {
                    Geometry geometry = new ObjectMapper().readValue(pages.getJSONObject(i).getJSONObject("geometry").toString(), Geometry.class);
                    parking.setDistance(distanceEarth(0.351295426580696, geometry.getCoordinates()[0], 46.58595804860371, geometry.getCoordinates()[1], 0, 0)); // Exemple :nom	"PALAIS DE JUSTICE" ===> distance ==0
                    parking.setGeometry(geometry);
                }
                parking.setField(field);
                parking.setRuntimeDetail(rumTimeDetails(parking.getField().getNom(), parking.getGeometry()));
                parkingList.add(parking);

            }

            System.err.println(parkingList);
            Collections.sort(parkingList);
            System.err.println(parkingList);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    RuntimeDetail rumTimeDetails(String nomParm, Geometry geometryParam) throws IOException, URISyntaxException {

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
                if(runtimeDetail.getNom().equals(nomParm) )
                    runtimeDetailVar = runtimeDetail;
                if(pages.getJSONObject(i).has("geometry") && geometryParam != null){
                    Geometry geometry = new ObjectMapper().readValue(pages.getJSONObject(i).getJSONObject("geometry").toString(), Geometry.class);
                    if(geometryParam.equals(geometry)) {
                        runtimeDetail.setGeometry(geometry);
                        runtimeDetailVar = runtimeDetail;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return runtimeDetailVar;
    }


}