package com.devian.biostabanalyzer.controllers;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.internal.BlockVar;
import com.devian.biostabanalyzer.services.CookieService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private final Gson gson = new Gson();

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
        model.addAttribute("block_vars", blockVars);
        BioModel bioModel = gson.fromJson(biomodel, BioModel.class);
        model.addAttribute("vars", bioModel.getVariables());
        return "index";
    }

    private static final String json_tmp = "{\n" +
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
