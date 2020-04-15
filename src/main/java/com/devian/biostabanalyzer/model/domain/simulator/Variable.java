package com.devian.biostabanalyzer.model.domain.simulator;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Variable {

    @SerializedName(value = "Id")
    private int id;
    @SerializedName(value = "Value")
    private int value;
}
