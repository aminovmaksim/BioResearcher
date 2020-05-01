package com.devian.biostabanalyzer.model.internal;

import lombok.Data;

@Data
public class TestResultsResponse {

    private String name;
    private String instanceId;
    private String runtimeStatus;
    private String customStatus;
    private String output;
    private String createdTime;
    private String lastUpdatedTime;

    public boolean isCompleted() {
        return runtimeStatus.equals("Completed");
    }
}
