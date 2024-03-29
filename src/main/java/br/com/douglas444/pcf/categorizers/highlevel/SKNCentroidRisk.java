package br.com.douglas444.pcf.categorizers.highlevel;

import br.com.douglas444.pcf.categorizers.classifier.SharedKernelNeighbours;
import br.com.douglas444.pcf.categorizers.commons.Util;
import br.ufu.facom.pcf.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SKNCentroidRisk implements HighLevelCategorizer, Configurable {

    private static final String THRESHOLD = "Threshold";
    private static final String FACTOR = "Factor";

    private static final double DEFAULT_THRESHOLD = 0.8;
    private static final double DEFAULT_FACTOR = 2;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public SKNCentroidRisk() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(THRESHOLD, DEFAULT_THRESHOLD);
        this.numericParameters.put(FACTOR, DEFAULT_FACTOR);
    }

    @Override
    public Category categorize(Context context) {

        double bayesianErrorEstimation = getValue(
                context.getPatternClusterSummary(),
                context.getClusterSummaries(),
                context.getKnownLabels());

        if (bayesianErrorEstimation > this.numericParameters.get(THRESHOLD)) {
            return Category.NOVELTY;
        } else {
            return Category.KNOWN;
        }
    }

    private double getValue(final ClusterSummary target,
                            final List<ClusterSummary> clusterSummaries,
                            final Set<Integer> knownLabels) {

        if (clusterSummaries.isEmpty()) {
            return 1;
        }

        final double risk = 1 - SharedKernelNeighbours.calculateProbability(
                target,
                clusterSummaries,
                knownLabels,
                this.numericParameters.get(FACTOR));

        return Util.calculateNormalizedError(knownLabels, risk);
    }
    @Override
    public HashMap<String, String> getNominalParameters() {
        return this.nominalParameters;
    }

    @Override
    public HashMap<String, Double> getNumericParameters() {
        return this.numericParameters;
    }
}
