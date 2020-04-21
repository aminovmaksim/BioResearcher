package com.devian.biostabanalyzer.services;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.domain.simulator.Variable;
import com.devian.biostabanalyzer.model.internal.ModelTest;
import com.devian.biostabanalyzer.model.internal.ModelTestStr;
import com.devian.biostabanalyzer.model.internal.TestResponse;
import com.devian.biostabanalyzer.model.network.SimulateRequest;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.devian.biostabanalyzer.model.internal.ModelTest.Action;
import static com.devian.biostabanalyzer.model.internal.ModelTest.Condition;

@Service
public class TestService {

    @Autowired
    Gson gson;

    @Autowired
    AnalyzeService analyzeService;

    public TestResponse runTest(BioModel bioModel, ModelTestStr str_test) {

        ModelTest modelTest = getTestObject(str_test, bioModel);

        TestResponse testResponse = new TestResponse();
        testResponse.setName(modelTest.getName());
        testResponse.setId(modelTest.getId());

        if (modelTest.getSyntaxError() != null) {
            testResponse.setSyntaxError(modelTest.getSyntaxError());
            testResponse.setTestSuccess(false);
        } else {
            testResponse = runTest(modelTest, bioModel);
        }

        return testResponse;
    }

    public List<TestResponse> runTests(BioModel bioModel, List<ModelTestStr> str_tests) {

        List<ModelTest> tests = new ArrayList<>();
        List<TestResponse> responses = new ArrayList<>();

        for (ModelTestStr str_test : str_tests) {
            ModelTest modelTest = getTestObject(str_test, bioModel);
            tests.add(modelTest);
        }

        for (ModelTest modelTest : tests) {
            TestResponse testResponse = new TestResponse();
            testResponse.setName(modelTest.getName());
            testResponse.setId(modelTest.getId());
            if (modelTest.getSyntaxError() != null) {
                testResponse.setSyntaxError(modelTest.getSyntaxError());
                testResponse.setTestSuccess(false);
                responses.add(testResponse);
            } else {
                responses.add(runTest(modelTest, bioModel));
            }
        }

        return responses;
    }

    public ModelTest getTestObject(ModelTestStr str_test, BioModel bioModel) {

        ModelTest modelTest = new ModelTest();
        modelTest.setName(str_test.getName());
        modelTest.setId(str_test.getId());

        String[] str = str_test.getTest().split(" ");

        int i = 0;
        while (i < str.length) {
            switch (str[i]) {
                case "when":
                    if ((i + 1) < str.length) {
                        String varName = str[i+1];
                        if (!varExists(str[i+1], bioModel)) {
                            modelTest.setSyntaxError("Variable " + varName + " does not exist");
                            return modelTest;
                        }
                        if ((i + 2) < str.length &&
                                (str[i+2].equals("==") || str[i+2].equals(">") || str[i+2].equals("<"))) {
                            String operation = str[i+2];
                            if ((i + 3) < str.length) {
                                int value;
                                try {
                                    value = Integer.parseInt(str[i + 3]);
                                } catch (NumberFormatException e) {
                                    modelTest.setSyntaxError("Expected INTEGER variable value at symbol " + getSymbolByWord(i+3,str));
                                    return modelTest;
                                }
                                Condition condition = new Condition();
                                condition.setVar(varName);
                                condition.setOperation(operation);
                                condition.setValue(value);
                                modelTest.setCondition(condition);
                                i = i + 4;
                            } else {
                                modelTest.setSyntaxError("Expected variable value at symbol " + getSymbolByWord(i+3,str));
                                return modelTest;
                            }
                        } else {
                            modelTest.setSyntaxError("Expected '==' or '<' or '>' at symbol " + getSymbolByWord(i+2,str));
                            return modelTest;
                        }
                    } else {
                        modelTest.setSyntaxError("Expected variable name at symbol " + getSymbolByWord(i+1,str));
                        return modelTest;
                    }
                    break;
                case "then":
                    if ((i + 1) < str.length) {
                        switch (str[i+1]) {
                            case "set":
                                if ((i + 2) < str.length) {
                                    String varName = str[i+2];
                                    if (!varExists(str[i+2], bioModel)) {
                                        modelTest.setSyntaxError("Variable " + varName + " does not exist");
                                        return modelTest;
                                    }
                                    if ((i + 3) < str.length) {
                                        int value;
                                        try {
                                            value = Integer.parseInt(str[i + 3]);
                                        } catch (NumberFormatException e) {
                                            modelTest.setSyntaxError("Expected INTEGER variable value at symbol " + getSymbolByWord(i+3,str));
                                            return modelTest;
                                        }
                                        Action action = new Action();
                                        action.setVar(varName);
                                        action.setType("set");
                                        action.setValue(value);
                                        modelTest.setAction(action);
                                        i = i + 4;
                                    } else {
                                        modelTest.setSyntaxError("Expected variable value at symbol " + getSymbolByWord(i+3,str));
                                        return modelTest;
                                    }
                                } else {
                                    modelTest.setSyntaxError("Expected variable name at symbol " + getSymbolByWord(i+2,str));
                                    return modelTest;
                                }
                                break;
                            case "block":
                                if ((i + 2) < str.length) {
                                    String varName = str[i+2];
                                    if (!varExists(str[i+2], bioModel)) {
                                        modelTest.setSyntaxError("Variable " + varName + " does not exist");
                                        return modelTest;
                                    }
                                    Action action = new Action();
                                    action.setVar(varName);
                                    action.setType("block");
                                    modelTest.setAction(action);
                                    i = i + 3;
                                } else {
                                    modelTest.setSyntaxError("Expected variable name at symbol " + getSymbolByWord(i+2,str));
                                    return modelTest;
                                }
                                break;
                            default:
                                modelTest.setSyntaxError("Expected action 'set' or 'block' at symbol " + getSymbolByWord(i+1,str));
                                return modelTest;
                        }
                    } else {
                        modelTest.setSyntaxError("Expected action 'set' or 'block' at symbol " + getSymbolByWord(i+1,str));
                        return modelTest;
                    }
                    break;
                case "expect":
                    if ((i + 1) < str.length) {
                        if (str[i+1].equals("stab")) {
                            modelTest.setExpectStab(true);
                            i = i + 2;
                        } else if (str[i+1].equals("unstab")) {
                            modelTest.setExpectStab(false);
                            i = i + 2;
                        } else {
                            modelTest.setSyntaxError("Expected 'stab' or 'unstab' at symbol " + getSymbolByWord(i+1,str) );
                            return modelTest;
                        }
                    }
                    break;
                default:
                    modelTest.setSyntaxError("Expected 'when' or 'then' or 'expect' at symbol " + getSymbolByWord(i,str));
                    return modelTest;
            }
        }

        if (modelTest.getExpectStab() == null && modelTest.getAction().getType().equals(Action.TYPE_BLOCK)) {
            modelTest.setSyntaxError("Missing 'expect' statement");
        }

        return modelTest;
    }

    public boolean varExists(String varName, BioModel bioModel) {
        for (BioModel.Variable var : bioModel.getVariables()) {
            if (var.getName().equals(varName)) {
                return true;
            }
        }
        return false;
    }

    public TestResponse runTest(ModelTest test, BioModel model) {

        TestResponse testResponse = new TestResponse();
        testResponse.setName(test.getName());
        testResponse.setId(test.getId());

        List<Variable> req_vars = new ArrayList<>();

        if (test.getAction().getType().equals(Action.TYPE_BLOCK)) {

            for (BioModel.Variable variable : model.getVariables()) {
                req_vars.add(Variable.builder().id(variable.getId()).value(0).build());
            }

            for (int i = 0; i < 100; i++) {
                boolean ready = processTest(req_vars, model, test);
                if (ready) {
                    boolean result = analyzeService.analyze(model).isStabilizing();
                    testResponse.setTestSuccess(result == test.getExpectStab());
                    return testResponse;
                }
                SimulateRequest simulateRequest = SimulateRequest.builder()
                        .model(model)
                        .variables(req_vars)
                        .build();
                req_vars = analyzeService.simulate(simulateRequest).getVariables();
            }
        } else {

            for (BioModel.Variable variable : model.getVariables()) {
                req_vars.add(Variable.builder().id(variable.getId()).value(0).build());
            }

            for (int i = 0; i < 100; i++) {
                boolean ready = processTest(req_vars, model, test);
                if (ready) {
                    testResponse.setTestSuccess(true);
                    return testResponse;
                }
                SimulateRequest simulateRequest = SimulateRequest.builder()
                        .model(model)
                        .variables(req_vars)
                        .build();
                req_vars = analyzeService.simulate(simulateRequest).getVariables();
            }

        }

        testResponse.setSyntaxError("Cannot get test result, maybe condition never executes");
        return testResponse;
    }

    public boolean processTest(List<Variable> req_vars, BioModel model, ModelTest test) {

        if (test.getAction().getType().equals(Action.TYPE_BLOCK)) {
            for (Variable var : req_vars) {
                if (test.getCondition().getVar().equals(getNameById(var.getId(), model))) {

                    BioModel.Variable variable = model.getVariables().get(getVariableIndex(test.getAction().getVar(), model));
                    int variable_value = req_vars.get(getSimulationVariableIndex(req_vars, variable.getId())).getValue();


                    if (test.getCondition().getOperation().equals("==")) {
                        if (test.getCondition().getValue().equals(var.getValue())) {
                            model.getVariables().get(getVariableIndex(test.getAction().getVar(), model))
                                    .setFormula(String.valueOf(variable_value));
                            return true;
                        }
                    }
                    if (test.getCondition().getOperation().equals(">")) {
                        if (var.getValue() > test.getCondition().getValue()) {
                            model.getVariables().get(getVariableIndex(test.getAction().getVar(), model))
                                    .setFormula(String.valueOf(variable_value));
                            return true;
                        }
                    }
                    if (test.getCondition().getOperation().equals("<")) {
                        if (var.getValue() < test.getCondition().getValue()) {
                            model.getVariables().get(getVariableIndex(test.getAction().getVar(), model))
                                    .setFormula(String.valueOf(variable_value));
                            return true;
                        }
                    }
                }
            }
        } else {
            for (Variable var : req_vars) {
                if (test.getCondition().getVar().equals(getNameById(var.getId(), model))) {
                    if (test.getCondition().getOperation().equals("==")) {
                        if (test.getCondition().getValue().equals(var.getValue())) {
                            return true;
                        }
                    }
                    if (test.getCondition().getOperation().equals(">")) {
                        if (var.getValue() > test.getCondition().getValue()) {
                            return true;
                        }
                    }
                    if (test.getCondition().getOperation().equals("<")) {
                        if (var.getValue() < test.getCondition().getValue()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Variable getSimulationVariable(List<Variable> simulation_vars, BioModel.Variable model_var) {
        for (Variable simulation_var : simulation_vars) {
            if (simulation_var.getId().equals(model_var.getId())) {
                return simulation_var;
            }
        }
        return null;
    }

    private int getSimulationVariableIndex(List<Variable> simulation_vars, Integer var_id) {
        for (int i = 0; i < simulation_vars.size(); i++) {
            if (simulation_vars.get(i).getId().equals(var_id)) {
                return i;
            }
        }
        return -1;
    }

    private String getNameById(Integer id, BioModel model) {
        for (BioModel.Variable var : model.getVariables()) {
            if (var.getId().equals(id)) {
                return var.getName();
            }
        }
        return null;
    }

    private Integer getIdByName(String name, BioModel model) {
        for (BioModel.Variable var : model.getVariables()) {
            if (var.getName().equals(name)) {
                return var.getId();
            }
        }
        return null;
    }

    private int getVariableIndex(Integer id, BioModel model) {
        for (int i = 0; i < model.getVariables().size(); i++) {
            if (id.equals(model.getVariables().get(i).getId())) {
                return i;
            }
        }
        return 0;
    }

    private int getVariableIndex(String name, BioModel model) {
        return getVariableIndex(getIdByName(name, model), model);
    }

    private int getSymbolByWord(int word, String[] words) {
        int res = 0;
        for (int i = 0; i < word; i++) {
            res += words[i].length();
        }
        return res;
    }
}
