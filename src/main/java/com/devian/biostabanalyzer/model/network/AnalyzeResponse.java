package com.devian.biostabanalyzer.model.network;

import com.devian.biostabanalyzer.model.domain.analyzer.Tick;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Data
@Builder
public class AnalyzeResponse {

    private static final String STATUS_STABILIZING = "Stabilizing";

    @SerializedName(value = "Time")
    private int time;
    @SerializedName(value = "ErrorMessages")
    private List<String> errorMessages;
    @SerializedName(value = "DebugMessages")
    private List<String> debugMessages;
    @SerializedName(value = "Status")
    private String status;
    @SerializedName(value = "Error")
    private String error;
    @SerializedName(value = "Ticks")
    private List<Tick> ticks;

    public boolean isStabilizing() {
        return status.equals(STATUS_STABILIZING);
    }
}
