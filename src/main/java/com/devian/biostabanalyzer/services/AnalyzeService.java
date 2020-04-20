package com.devian.biostabanalyzer.services;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.domain.simulator.Variable;
import com.devian.biostabanalyzer.model.internal.Chart;
import com.devian.biostabanalyzer.model.network.AnalyzeResponse;
import com.devian.biostabanalyzer.model.network.SimulateRequest;
import com.devian.biostabanalyzer.model.network.SimulateResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.devian.biostabanalyzer.model.internal.Chart.*;

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

    public Chart simulate(int steps, BioModel bioModel) {

        List<Variable> req_vars = new ArrayList<>();
        for (BioModel.Variable variable : bioModel.getVariables()) {
            req_vars.add(Variable.builder().id(variable.getId()).value(0).build());
        }

        SimulateRequest request = SimulateRequest.builder()
                .model(bioModel)
                .variables(req_vars)
                .build();

        String url = URL + "Simulate";

        List<SimulateResponse> responses = new ArrayList<>();
        responses.add(SimulateResponse.builder().variables(request.getVariables()).build());

        for (int i = 0; i < steps; i++) {
            HttpEntity<SimulateRequest> entity = new HttpEntity<>(request);
            String json = this.restTemplate.postForObject(url, entity, String.class);
            SimulateResponse response = gson.fromJson(json, SimulateResponse.class);
            responses.add(response);
            request.setVariables(response.getVariables());
        }

        System.out.println(gson.toJson(responses));

        List<Integer> labels = new ArrayList<>();
        for (int i = 1; i <= steps; i++) {
            labels.add(i);
        }

        List<ChartDataset> datasets = new ArrayList<>();
        for (BioModel.Variable variable : bioModel.getVariables()) {
            List<Integer> data = new ArrayList<>();
            for (SimulateResponse response : responses) {
                for (Variable var : response.getVariables()) {
                    if (var.getId().equals(variable.getId())) {
                        data.add(var.getValue());
                    }
                }
            }
            ChartDataset dataset = ChartDataset.builder()
                    .label(variable.getName())
                    .fill(false)
                    .lineTension(0)
                    .borderColor(getRandomColor())
                    .data(data)
                    .build();
            datasets.add(dataset);
        }

        ChartData chartData = ChartData.builder()
                .labels(labels)
                .datasets(datasets)
                .build();

        Chart chart = Chart.builder()
                .type("line")
                .data(chartData)
                .build();

        return chart;
    }

    private String getRandomColor() {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        return String.format("#%06x", rand_num);
    }

}
