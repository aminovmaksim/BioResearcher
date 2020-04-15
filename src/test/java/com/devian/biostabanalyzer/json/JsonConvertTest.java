package com.devian.biostabanalyzer.json;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.domain.analyzer.Tick;
import com.devian.biostabanalyzer.model.domain.simulator.Variable;
import com.devian.biostabanalyzer.model.network.AnalyzeResponse;
import com.devian.biostabanalyzer.model.network.SimulateRequest;
import com.devian.biostabanalyzer.model.network.SimulateResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.*;

@SpringBootTest
public class JsonConvertTest {

    private final Gson gson = new Gson();

    @Test
    void analyzeRequestTest() {
        String json = null;
        try {
            File file = ResourceUtils.getFile("classpath:examples/analyze_request.json");
            Scanner scanner = new Scanner(file);
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            json = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(json);

        BioModel bioModel = gson.fromJson(json, BioModel.class);
        assertEquals("model 1", bioModel.getName());

        BioModel.Variable variable = bioModel.getVariables().get(0);
        assertEquals("I", variable.getName());
        assertEquals((Integer) 16, variable.getId());
        assertEquals((Integer) 0, variable.getRangeFrom());
        assertEquals((Integer) 4, variable.getRangeTo());
        assertEquals("2", variable.getFormula());

        BioModel.Relationship relationship = bioModel.getRelationships().get(0);
        assertEquals((Integer) 19, relationship.getId());
        assertEquals((Integer) 16, relationship.getFromVariable());
        assertEquals((Integer) 18, relationship.getToVariable());
        assertEquals("Activator", relationship.getType());
    }

    @Test
    void analyzeResponseTest() {
        String json = null;
        try {
            File file = ResourceUtils.getFile("classpath:examples/analyze_response.json");
            Scanner scanner = new Scanner(file);
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            json = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(json);

        AnalyzeResponse response = gson.fromJson(json, AnalyzeResponse.class);
        assertEquals((Integer) 0, response.getTime());
        assertNull(response.getErrorMessages());
        assertEquals("Stabilizing", response.getStatus());
        assertTrue(response.isStabilizing());

        Tick tick = response.getTicks().get(0);
        assertEquals((Integer) 2, (Integer) tick.getTime());
        assertEquals((Integer) 3, (Integer) tick.getVariables().size());
    }

    @Test
    void simulateRequestTest() {
        String json = null;
        try {
            File file = ResourceUtils.getFile("classpath:examples/simulate_request.json");
            Scanner scanner = new Scanner(file);
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            json = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(json);

        SimulateRequest request = gson.fromJson(json, SimulateRequest.class);
        assertEquals("model 1", request.getModel().getName());

        Variable variable = request.getVariables().get(0);
        assertEquals((Integer) 56, variable.getId());
        assertEquals((Integer) 0, variable.getValue());
    }

    @Test
    void simulateResponseTest() {
        String json = null;
        try {
            File file = ResourceUtils.getFile("classpath:examples/simulate_response.json");
            Scanner scanner = new Scanner(file);
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            json = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(json);

        SimulateResponse response = gson.fromJson(json, SimulateResponse.class);
        assertEquals((Integer) 4, (Integer) response.getVariables().size());
        assertNull(response.getErrorMessages());
        assertNotNull(response.getDebugMessages());

        Variable variable = response.getVariables().get(0);
        assertEquals((Integer) 56, variable.getId());
        assertEquals((Integer) 1, variable.getValue());
    }
}