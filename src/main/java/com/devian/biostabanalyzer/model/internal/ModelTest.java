package com.devian.biostabanalyzer.model.internal;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ModelTest {

    @SerializedName(value = "Id")
    private int id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Cond")
    private Condition condition;
    @SerializedName(value = "Act")
    private Action action;
    @SerializedName(value = "ExpectStab")
    private Boolean expectStab;
    @SerializedName(value = "SyntaxError")
    private String syntaxError;

    @Data
    public static class Condition {

        @SerializedName("Var")
        private String var;
        @SerializedName("Operation")
        private String operation;
        @SerializedName("Value")
        private Integer value;
    }

    @Data
    public static class Action {
        @SerializedName("Var")
        private String var;
        @SerializedName("Type")
        private String type;
        @SerializedName("Value")
        private Integer value;

        public static final String TYPE_BLOCK = "block";
        public static final String TYPE_SET = "set";
    }
}
