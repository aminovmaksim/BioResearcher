package com.devian.biostabanalyzer.model.internal;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class TestResponse {

    @SerializedName("Id")
    private Integer id;
    @SerializedName("Name")
    private String name;
    @SerializedName("SyntaxError")
    private String syntaxError;
    @SerializedName("TestSuccess")
    private Boolean testSuccess;
}
