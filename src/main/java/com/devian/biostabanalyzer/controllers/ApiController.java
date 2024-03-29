package com.devian.biostabanalyzer.controllers;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.internal.BlockVar;
import com.devian.biostabanalyzer.model.internal.ModelTestStr;
import com.devian.biostabanalyzer.services.AnalyzeService;
import com.devian.biostabanalyzer.services.CookieService;
import com.devian.biostabanalyzer.services.TestService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.List;

@RestController
public class ApiController {

    @Autowired
    TestService testService;

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
        Type itemsListType = new TypeToken<List<BlockVar>>() {
        }.getType();
        List<BlockVar> blockVars = gson.fromJson(block_vars, itemsListType);
        int stepsCount = Integer.parseInt(steps);

        for (BlockVar blockVar : blockVars) {
            for (int i = 0; i < bioModel.getVariables().size(); i++) {
                if (bioModel.getVariables().get(i).getId().equals(blockVar.getId())) {
                    bioModel.getVariables().get(i).setFormula(String.valueOf(blockVar.getValue()));
                }
            }
        }

        return gson.toJson(analyzeService.simulateBySteps(stepsCount, bioModel));
    }

    @GetMapping("/test_all")
    public String test_all(
            @CookieValue(value = "biomodel") String biomodel,
            @RequestHeader(value = "tests") String tests
    ) {
        BioModel bioModel = gson.fromJson(biomodel, BioModel.class);
        Type itemsListType = new TypeToken<List<ModelTestStr>>() {
        }.getType();
        List<ModelTestStr> str_tests = gson.fromJson(tests, itemsListType);

        return gson.toJson(testService.runTests(bioModel, str_tests));
    }

    @GetMapping("/test")
    public String test(
            @CookieValue(value = "biomodel") String biomodel,
            @RequestHeader(value = "test") String test
    ) {
        BioModel bioModel = gson.fromJson(biomodel, BioModel.class);
        ModelTestStr str_test = gson.fromJson(test, ModelTestStr.class);

        return gson.toJson(testService.runTest(bioModel, str_test));
    }

    @GetMapping("/save_tests")
    public void saveTest(
            HttpServletResponse response,
            @RequestHeader(value = "new_tests") String new_tests) {

        Type itemsListType = new TypeToken<List<ModelTestStr>>() {}.getType();

        List<ModelTestStr> tests = gson.fromJson(new_tests, itemsListType);
        String cookie = gson.toJson(tests);
        CookieService.setCookie("saved_tests", cookie, response);
    }

    @GetMapping("/get_saved_tests")
    public String getSavedTest(
            @CookieValue(value = "saved_tests", required = false) String cookie_tests
    ) {
        if (cookie_tests == null) {
            return "[]";
        } else {
            cookie_tests = CookieService.getCookie(cookie_tests);
            return cookie_tests;
        }
    }
}
