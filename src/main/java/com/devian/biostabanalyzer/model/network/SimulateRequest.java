package com.devian.biostabanalyzer.model.network;

import com.devian.biostabanalyzer.model.domain.BioModel;
import com.devian.biostabanalyzer.model.domain.simulator.Variable;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SimulateRequest {

    @SerializedName(value = "Model")
    private BioModel model;
    @SerializedName(value = "Variables")
    private List<Variable> variables;
}
