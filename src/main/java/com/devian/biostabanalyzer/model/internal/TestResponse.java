package com.devian.biostabanalyzer.model.internal;

import lombok.Data;

@Data
public class TestResponse {

    private String name;
    private String syntaxError;
    private boolean testSuccess;
}
