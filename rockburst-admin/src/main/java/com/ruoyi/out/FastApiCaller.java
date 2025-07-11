package com.ruoyi.out;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

public class FastApiCaller {

    private final RestTemplate restTemplate = new RestTemplate();

    public String callContourDxf(String serverUrl, ContourRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ContourRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                serverUrl + "/contour-dxf/", httpEntity, String.class);

        return response.getBody();
    }

    public String callBigFault(String serverUrl, ContourRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ContourRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                serverUrl + "/big-fault-dxf/", httpEntity, String.class);

        return response.getBody();
    }

    public String callSmallFault(String serverUrl, ContourRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ContourRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                serverUrl + "/small-fault-dxf/", httpEntity, String.class);

        return response.getBody();
    }

    public String callGob(String serverUrl, ContourRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ContourRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                serverUrl + "/gob-dxf/", httpEntity, String.class);

        return response.getBody();
    }

    public String callFold(String serverUrl, ContourRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ContourRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                serverUrl + "/fold-dxf/", httpEntity, String.class);

        return response.getBody();
    }

    public String callCoalPillar(String serverUrl, ContourRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ContourRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                serverUrl + "/coal-pillar-dxf/", httpEntity, String.class);

        return response.getBody();
    }
}
