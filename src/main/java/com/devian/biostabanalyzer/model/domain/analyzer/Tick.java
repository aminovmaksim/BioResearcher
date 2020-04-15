package com.devian.biostabanalyzer.model.domain.analyzer;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Data
@Builder
public class Tick {

    @SerializedName(value = "Time")
    private int time;
    @SerializedName(value = "Variables")
    private List<TickVariable> variables;

    @Data
    @Builder
    public static class TickVariable {

        @SerializedName(value = "Id")
        private int id;
        @SerializedName(value = "Lo")
        private int lo;
        @SerializedName(value = "Hi")
        private int hi;
    }
}
