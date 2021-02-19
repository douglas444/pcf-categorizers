package br.com.douglas444.metacategorizer.highlevel;

import br.com.douglas444.util.BayesianErrorEstimation;
import br.com.douglas444.mltk.datastructure.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DistanceCategorizer implements HighLevelCategorizer, Configurable {

    private static final String THRESHOLD = "Threshold";
    private static final double DEFAULT_THRESHOLD = 0.8;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public DistanceCategorizer() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(THRESHOLD, DEFAULT_THRESHOLD);
    }

    @Override
    public Category categorize(Context context) {

        double bayesianErrorEstimation = getValue(context.getPatternClusterSummary(), context.getClusterSummaries(),
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

        final Sample targetConceptCentroid = new Sample(targetClusterSummary.calculateCentroidAttributes(), null);

        final List<Sample> knownConceptsCentroids = knownClusterSummaries
                .stream()
                .map(clusterSummary -> new Sample(clusterSummary.calculateCentroidAttributes(), clusterSummary.getLabel()))
                .collect(Collectors.toList());

        double bayesianErrorEstimation;
        if (knownConceptsCentroids.isEmpty()) {
            bayesianErrorEstimation = 1;
        } else {
            bayesianErrorEstimation = BayesianErrorEstimation
                    .distanceProbability(targetConceptCentroid, knownConceptsCentroids, knownLabels);
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
