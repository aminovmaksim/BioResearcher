package com.devian.biostabanalyzer.model.domain;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Data
@Builder
public class BioModel implements Cloneable {

    @SerializedName(value = "Name", alternate = "name")
    private String name;
    @SerializedName(value = "Variables", alternate = "variables")
    private List<Variable> variables;
    @SerializedName(value = "Relationships", alternate = "relationships")
    private List<Relationship> relationships;

    @Data
    @Builder
    public static class Variable {

        @SerializedName(value = "Name", alternate = "name")
        private String name;
        @SerializedName(value = "Id", alternate = "id")
        private Integer id;
        @SerializedName(value = "RangeFrom", alternate = "rangeFrom")
        private Integer rangeFrom;
        @SerializedName(value = "RangeTo", alternate = "rangeTo")
        private Integer rangeTo;
        @SerializedName(value = "Formula", alternate = "formula")
        private String formula;
        private boolean blocked;
        private Integer value;
    }

    @Data
    @Builder
    public static class Relationship {

        @SerializedName(value = "Id", alternate = "id")
        private Integer id;
        @SerializedName(value = "FromVariable", alternate = "fromVariableId")
        private Integer fromVariable;
        @SerializedName(value = "ToVariable", alternate = "toVariableId")
        private Integer toVariable;
        @SerializedName(value = "Type", alternate = "type")
        private String type;
    }
}
