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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.devian.biostabanalyzer.model.internal.Chart.ChartData;
import static com.devian.biostabanalyzer.model.internal.Chart.ChartDataset;

@Service
public class AnalyzeService {

    public static final String URL = "https://bmafunctionscore.azurewebsites.net/api/";
    public static final String URL_SIMULATE = URL + "Simulate";
    public static final String URL_ANALYZE = URL + "Analyze";

    private final Gson gson;
    private final RestTemplate restTemplate;

    public AnalyzeService(RestTemplateBuilder restTemplateBuilder, Gson gson) {
        this.restTemplate = restTemplateBuilder.build();
        this.gson = gson;
    }

    public AnalyzeResponse analyze(BioModel model) {
        HttpEntity<BioModel> entity = new HttpEntity<>(model);
        String response = this.restTemplate.postForObject(URL_ANALYZE, entity, String.class);
        return gson.fromJson(response, AnalyzeResponse.class);
    }

    public SimulateResponse simulate(SimulateRequest request) {
        HttpEntity<SimulateRequest> entity = new HttpEntity<>(request);
        String json = this.restTemplate.postForObject(URL_SIMULATE, entity, String.class);
        return gson.fromJson(json, SimulateResponse.class);
    }

    public Chart simulateBySteps(int steps, BioModel bioModel) {

        List<Variable> req_vars = new ArrayList<>();
        for (BioModel.Variable variable : bioModel.getVariables()) {
            req_vars.add(Variable.builder().id(variable.getId()).value(0).build());
        }

        SimulateRequest request = SimulateRequest.builder()
                .model(bioModel)
                .variables(req_vars)
                .build();

        List<SimulateResponse> responses = new ArrayList<>();
        responses.add(SimulateResponse.builder().variables(request.getVariables()).build());

        for (int i = 0; i < steps; i++) {
            SimulateResponse response = simulate(request);
            responses.add(response);
            request.setVariables(response.getVariables());
        }

        return getSimulationChart(responses, bioModel);
    }

    public Chart getSimulationChart(List<SimulateResponse> responses, BioModel bioModel) {

        if (responses == null) {
            return null;
        }

        List<Integer> labels = new ArrayList<>();
        for (int i = 1; i <= responses.size(); i++) {
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
