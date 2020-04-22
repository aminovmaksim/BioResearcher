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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
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
                       @RequestParam(required = false) String json,
                       @CookieValue(value = "biomodel") String model_from_cookie,
                       HttpServletResponse response) {
        if (json != null) {
            CookieService.setCookie("biomodel", gson.toJson(gson.fromJson(json, BioModel.class)), response);
            return "redirect:/";
        } else if (model_from_cookie != null) {
            json = model_from_cookie;
        } else {

        }
        BioModel bioModel = gson.fromJson(json, BioModel.class);
        model.addAttribute("vars", bioModel.getVariables());
        return "index";
    }

    @GetMapping("/block_var")
    public String blockVar(Model model,
                           @CookieValue(value = "biomodel") String biomodel,
                           @RequestParam(value = "vars") String json) {
        json = CookieService.getCookie(json);

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
            analyzeResponse.setStatus("Failed to prove stability");
        }

        model.addAttribute("block_vars", blockVars);
        model.addAttribute("stab", analyzeResponse.getStatus());
        model.addAttribute("vars", bioModel.getVariables());
        return "index";
    }

    @GetMapping("/new_model")
    public String new_model() {
        return "new_model";
    }
}
