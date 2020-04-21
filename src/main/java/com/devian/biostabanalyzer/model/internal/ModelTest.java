package com.devian.biostabanalyzer.model.internal;

import lombok.Data;

@Data
public class ModelTest {

    private int id;
    private String name;
    private Condition condition;
    private Action action;
    private Boolean expectStab;

    private String syntaxError;

    @Data
    public static class Condition {
        private String var;
        private String operation;
        private Integer value;
    }

    @Data
    public static class Action {
        private String var;
        private String type;
        private Integer value;

        public static final String TYPE_BLOCK = "block";
        public static final String TYPE_SET = "set";
    }
}
