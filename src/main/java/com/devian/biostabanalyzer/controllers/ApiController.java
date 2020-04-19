package com.devian.biostabanalyzer.controllers;

import com.devian.biostabanalyzer.model.internal.Chart;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.devian.biostabanalyzer.model.internal.Chart.*;

@RestController
public class ApiController {

    @Autowired
    Gson gson;

    @GetMapping(value = "/simulate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String simulate(
            @CookieValue(value = "biomodel") String biomodel,
            @RequestHeader(value = "block_vars") String block_vars,
            @RequestHeader(value = "steps") String steps) {

        System.out.println(block_vars);
        System.out.println(steps);

        List<Integer> labels = new ArrayList<>();
        labels.add(1); labels.add(2); labels.add(3);

        List<Integer> data1 = new ArrayList<>();
        data1.add(3); data1.add(3); data1.add(4);

        List<Integer> data2 = new ArrayList<>();
        data2.add(0); data2.add(2); data2.add(4);

        List<ChartDataset> datasets = new ArrayList<>();
        datasets.add(ChartDataset.builder().label("A").fill(false).lineTension(0).data(data1).build());
        datasets.add(ChartDataset.builder().label("B").fill(false).lineTension(0).data(data2).build());


        ChartData chartData = ChartData.builder()
                .labels(labels)
                .datasets(datasets)
                .build();

        Chart chart = Chart.builder()
                .type("line")
                .data(chartData)
                .build();

        return gson.toJson(chart);
    }

}
