package com.devian.biostabanalyzer.model.domain;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Data
@Builder
public class BioModel {

    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Variables")
    private List<Variable> variables;
    @SerializedName(value = "Relationships")
    private List<Relationship> relationships;

    @Data
    @Builder
    public static class Variable {

        @SerializedName(value = "Name")
        private String name;
        @SerializedName(value = "Id")
        private Integer id;
        @SerializedName(value = "RangeFrom")
        private Integer rangeFrom;
        @SerializedName(value = "RangeTo")
        private Integer rangeTo;
        @SerializedName(value = "Formula")
        private String formula;
    }

    @Data
    @Builder
    public static class Relationship {

        @SerializedName(value = "Id")
        private Integer id;
        @SerializedName(value = "FromVariable")
        private Integer fromVariable;
        @SerializedName(value = "ToVariable")
        private Integer toVariable;
        @SerializedName(value = "Type")
        private String type;
    }
}
