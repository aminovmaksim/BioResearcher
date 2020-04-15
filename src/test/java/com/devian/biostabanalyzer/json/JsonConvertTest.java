package com.devian.biostabanalyzer.json;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.*;

@SpringBootTest
public class JsonConvertTest {

    @Autowired
    ResourceLoader resourceLoader;

    @Test
    void BioModelTest() {
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
        Gson gson = new Gson();

        BioModel bioModel = BioModel.builder()
                .name("Model 1")
                .build();
    }
}
