package com.devian.biostabanalyzer.model.internal;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class TestRequest {

    @SerializedName(value = "Model")
    private BioModel model;
    @SerializedName(value = "Tests")
    private List<ModelTest> tests;
}
