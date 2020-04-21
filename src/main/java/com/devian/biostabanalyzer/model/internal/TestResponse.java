package com.devian.biostabanalyzer.model.internal;

import lombok.Data;

@Data
public class TestResponse {

    private Integer id;
    private String name;
    private String syntaxError;
    private Boolean testSuccess;
}
