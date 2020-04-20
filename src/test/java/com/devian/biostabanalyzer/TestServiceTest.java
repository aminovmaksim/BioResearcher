package com.devian.biostabanalyzer;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.internal.ModelTest;
import com.devian.biostabanalyzer.model.internal.ModelTestStr;
import com.devian.biostabanalyzer.model.internal.TestResponse;
import com.devian.biostabanalyzer.services.TestService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;

@SpringBootTest
public class TestServiceTest {

    @Autowired
    Gson gson;

    @Autowired
    TestService testService;

    @Test
    void test() {
        BioModel bioModel = gson.fromJson(json, BioModel.class);

        List<ModelTestStr> str_tests = new ArrayList<>();

        ModelTestStr modelTestStr = new ModelTestStr();
        modelTestStr.setName("Test 1");
        modelTestStr.setTest("when B_active > 2 then block B_active expect stab");

        str_tests.add(modelTestStr);

        ModelTest modelTest = testService.getTestObject(modelTestStr, bioModel);

        assertNull(modelTest.getSyntaxError());

        List<TestResponse> responses = testService.runTests(bioModel, str_tests);

        System.out.println(gson.toJson(responses));
    }

    public static final String json = "{\"Name\":\"model 1\",\"Variables\":[{\"Name\":\"A\",\"Id\":79,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"\"},{\"Name\":\"B_active\",\"Id\":80,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"(4-var(84)+(1-1))\"},{\"Name\":\"C\",\"Id\":81,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"4*(var(79)-var(80))\"},{\"Name\":\"O\",\"Id\":82,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"\"},{\"Name\":\"I\",\"Id\":83,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"0\"},{\"Name\":\"B_inactive\",\"Id\":84,\"RangeFrom\":0,\"RangeTo\":4,\"Formula\":\"4*((var(79)+(4-2))-var(80))\"}],\"Relationships\":[{\"Id\":85,\"FromVariable\":83,\"ToVariable\":79,\"Type\":\"Activator\"},{\"Id\":86,\"FromVariable\":80,\"ToVariable\":81,\"Type\":\"Inhibitor\"},{\"Id\":87,\"FromVariable\":79,\"ToVariable\":81,\"Type\":\"Activator\"},{\"Id\":88,\"FromVariable\":81,\"ToVariable\":82,\"Type\":\"Activator\"},{\"Id\":89,\"FromVariable\":79,\"ToVariable\":84,\"Type\":\"Activator\"},{\"Id\":90,\"FromVariable\":84,\"ToVariable\":80,\"Type\":\"Inhibitor\"},{\"Id\":91,\"FromVariable\":80,\"ToVariable\":84,\"Type\":\"Inhibitor\"}]}";
}
