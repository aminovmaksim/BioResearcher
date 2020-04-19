package com.devian.biostabanalyzer.services;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.network.AnalyzeResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnalyzeService {

    @Autowired
    Gson gson;

    public static final String URL = "https://bmafunctionscore.azurewebsites.net/api/";

    private final RestTemplate restTemplate;

    public AnalyzeService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public AnalyzeResponse analyze(BioModel model) {
        String url = URL + "Analyze";
        HttpEntity<BioModel> entity = new HttpEntity<>(model);

        String response = this.restTemplate.postForObject(url, entity, String.class);
        System.out.println(response);

        return gson.fromJson(response, AnalyzeResponse.class);
    }

}
