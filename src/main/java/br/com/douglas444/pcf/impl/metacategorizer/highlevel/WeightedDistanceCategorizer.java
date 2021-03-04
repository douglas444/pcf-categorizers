package br.com.douglas444.pcf.impl.metacategorizer.highlevel;

import br.com.douglas444.pcf.impl.estimators.ProbabilityByEuclideanDistanceWeightedBySTDV;
import br.ufu.facom.pcf.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WeightedDistanceCategorizer implements HighLevelCategorizer, Configurable {

    private static final String THRESHOLD = "Threshold";
    private static final String DIMENSIONALITY = "Dimensionality";

    private static final double DEFAULT_THRESHOLD = 0.8;
    private static final double DEFAULT_DIMENSIONALITY = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public WeightedDistanceCategorizer() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(THRESHOLD, DEFAULT_THRESHOLD);
    }

    @Override
    public Category categorize(Context context) {

        final double bayesianErrorEstimation = getValue(
                context.getPatternClusterSummary(),
                context.getClusterSummaries(),
                context.getKnownLabels());

        if (bayesianErrorEstimation > this.numericParameters.get(THRESHOLD)) {
            return Category.NOVELTY;
        } else {
            return Category.KNOWN;
        }
    }

    public double getValue(final ClusterSummary targetClusterSummary,
                           final List<ClusterSummary> knownClusterSummaries,
                           final Set<Integer> knownLabels) {

        final double bayesianErrorEstimation;

        if (knownClusterSummaries.isEmpty()) {
            bayesianErrorEstimation = 1;
        } else {

            bayesianErrorEstimation = ProbabilityByEuclideanDistanceWeightedBySTDV.estimateError(
                    targetClusterSummary,
                    knownClusterSummaries,
                    knownLabels,
                    this.numericParameters.get(DIMENSIONALITY).intValue());

        }

        return bayesianErrorEstimation;
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
