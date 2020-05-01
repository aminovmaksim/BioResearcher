package com.devian.biostabanalyzer.model.internal;

import lombok.Data;

@Data
public class TestStartResponse {

    private String id;
    private String statusQueryGetUri;
    private String sendEventPostUri;
    private String terminatePostUri;
    private String purgeHistoryDeleteUri;
}
