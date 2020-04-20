package com.devian.biostabanalyzer.controllers;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.domain.simulator.Variable;
import com.devian.biostabanalyzer.model.internal.BlockVar;
import com.devian.biostabanalyzer.model.internal.Chart;
import com.devian.biostabanalyzer.model.network.SimulateRequest;
import com.devian.biostabanalyzer.services.AnalyzeService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.devian.biostabanalyzer.model.internal.Chart.*;

@RestController
public class ApiController {

    @Autowired
    AnalyzeService analyzeService;

    @Autowired
    Gson gson;

    @GetMapping(value = "/simulate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String simulate(
            @CookieValue(value = "biomodel") String biomodel,
            @RequestHeader(value = "block_vars") String block_vars,
            @RequestHeader(value = "steps") String steps) {

        BioModel bioModel = gson.fromJson(biomodel, BioModel.class);
        Type itemsListType = new TypeToken<List<BlockVar>>() {}.getType();
        List<BlockVar> blockVars = gson.fromJson(block_vars, itemsListType);
        int stepsCount = Integer.parseInt(steps);

        for (BlockVar blockVar : blockVars) {
            for (int i = 0; i < bioModel.getVariables().size(); i++) {
                if (bioModel.getVariables().get(i).getId().equals(blockVar.getId())) {
                    bioModel.getVariables().get(i).setFormula(String.valueOf(blockVar.getValue()));
                }
            }
        }

        return gson.toJson(analyzeService.simulate(stepsCount, bioModel));
    }

}
