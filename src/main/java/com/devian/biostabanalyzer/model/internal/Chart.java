package com.devian.biostabanalyzer.model.internal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Chart {

    private String type = "line";
    private ChartData data;

    @Data
    @Builder
    public static class ChartData {
        private List<Integer> labels;
        private List<ChartDataset> datasets;
    }

    @Data
    @Builder
    public static class ChartDataset {
        private String label;
        private String borderColor;
        private List<Integer> data;
        private boolean fill = false;
        private float lineTension = 0;
    }
}
