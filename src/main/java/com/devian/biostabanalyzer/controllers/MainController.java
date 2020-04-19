package com.devian.biostabanalyzer.controllers;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.internal.BlockVar;
import com.devian.biostabanalyzer.model.network.AnalyzeResponse;
import com.devian.biostabanalyzer.services.AnalyzeService;
import com.devian.biostabanalyzer.services.CookieService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    AnalyzeService analyzeService;

    @Autowired
    Gson gson;

    @RequestMapping("/")
    public String main(Model model,
                       @RequestBody (required = false) String json,
                       HttpServletResponse response) {
        if (json == null) {
            // todo обработать
        }

        json = json_tmp;

        CookieService.setCookie("biomodel", json, response);
        BioModel bioModel = gson.fromJson(json, BioModel.class);
        model.addAttribute("vars", bioModel.getVariables());
        return "index";
    }

    @GetMapping("/block_var")
    public String blockVar(Model model,
                           @CookieValue(value = "biomodel") String biomodel,
                           @RequestParam  (value = "vars") String json) {
        biomodel = CookieService.getCookie(biomodel);

        json = URLDecoder.decode(json, StandardCharsets.UTF_8);
        System.out.println(json);

        Type itemsListType = new TypeToken<List<BlockVar>>() {}.getType();
        List<BlockVar> blockVars = gson.fromJson(json, itemsListType);
        BioModel bioModel = gson.fromJson(biomodel, BioModel.class);

        BioModel analyzeRequest = gson.fromJson(biomodel, BioModel.class);
        for (BlockVar blockVar : blockVars) {
            for (int i = 0; i < analyzeRequest.getVariables().size(); i++) {
                if (analyzeRequest.getVariables().get(i).getId().equals(blockVar.getId())) {
                    analyzeRequest.getVariables().get(i).setFormula(String.valueOf(blockVar.getValue()));
                    bioModel.getVariables().get(i).setBlocked(true);
                    bioModel.getVariables().get(i).setValue(blockVar.getValue());
                }
            }
        }
        AnalyzeResponse analyzeResponse = analyzeService.analyze(analyzeRequest);
        if (analyzeResponse.getStatus().equals("Error")) {
            analyzeResponse.setStatus("Not stabilizing");
        }
        System.out.println(gson.toJson(bioModel));

        model.addAttribute("block_vars", blockVars);
        model.addAttribute("stab", analyzeResponse.getStatus());
        model.addAttribute("vars", bioModel.getVariables());
        return "index";
    }

    public static final String json_tmp = "{\"Name\":\"model 1\",\"Variables\":[{\"Name\":\"A\",\"Id\":79,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"\"},{\"Name\":\"B_active\",\"Id\":80,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"(4-var(84)+(1-1))\"},{\"Name\":\"C\",\"Id\":81,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"4*(var(79)-var(80))\"},{\"Name\":\"O\",\"Id\":82,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"\"},{\"Name\":\"I\",\"Id\":83,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"0\"},{\"Name\":\"B_inactive\",\"Id\":84,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"4*((var(79)+(4-2))-var(80))\"}],\"Relationships\":[{\"Id\":85,\"FromVariable\":83,\"ToVariable\":79,\"Type\":\"Activator\"},{\"Id\":86,\"FromVariable\":80,\"ToVariable\":81,\"Type\":\"Inhibitor\"},{\"Id\":87,\"FromVariable\":79,\"ToVariable\":81,\"Type\":\"Activator\"},{\"Id\":88,\"FromVariable\":81,\"ToVariable\":82,\"Type\":\"Activator\"},{\"Id\":89,\"FromVariable\":79,\"ToVariable\":84,\"Type\":\"Activator\"},{\"Id\":90,\"FromVariable\":84,\"ToVariable\":80,\"Type\":\"Inhibitor\"},{\"Id\":91,\"FromVariable\":80,\"ToVariable\":84,\"Type\":\"Inhibitor\"}]}";

    private static final String json_tmp2 = "{\n" +
            "  \"Name\": \"model 1\",\n" +
            "  \"Variables\": [\n" +
            "    {\n" +
            "      \"Name\": \"I\",\n" +
            "      \"Id\": 16,\n" +
            "      \"RangeFrom\": 0,\n" +
            "      \"RangeTo\": 4,\n" +
            "      \"Formula\": \"2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"O\",\n" +
            "      \"Id\": 17,\n" +
            "      \"RangeFrom\": 0,\n" +
            "      \"RangeTo\": 4,\n" +
            "      \"Formula\": \"\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"A\",\n" +
            "      \"Id\": 18,\n" +
            "      \"RangeFrom\": 0,\n" +
            "      \"RangeTo\": 4,\n" +
            "      \"Formula\": \"\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"Relationships\": [\n" +
            "    {\n" +
            "      \"Id\": 19,\n" +
            "      \"FromVariable\": 16,\n" +
            "      \"ToVariable\": 18,\n" +
            "      \"Type\": \"Activator\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"Id\": 20,\n" +
            "      \"FromVariable\": 18,\n" +
            "      \"ToVariable\": 17,\n" +
            "      \"Type\": \"Activator\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
