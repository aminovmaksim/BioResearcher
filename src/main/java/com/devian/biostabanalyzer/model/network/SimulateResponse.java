package com.devian.biostabanalyzer.model.network;

import com.devian.biostabanalyzer.model.domain.simulator.Variable;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SimulateResponse {

    @SerializedName(value = "Variables")
    private List<Variable> variables;
    @SerializedName(value = "ErrorMessages")
    private List<String> errorMessages;
    @SerializedName(value = "DebugMessages")
    private List<String> debugMessages;
}
