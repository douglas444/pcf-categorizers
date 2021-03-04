package br.com.douglas444.pcf.impl.metacategorizer.highlevel;

import br.com.douglas444.pcf.impl.estimators.ProbabilityByEuclideanDistance;
import br.com.douglas444.pcf.impl.commons.TypeConversion;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DistanceCategorizer implements HighLevelCategorizer, Configurable {

    private static final String THRESHOLD = "Threshold";
    private static final String DIMENSIONALITY = "Dimensionality";

    private static final double DEFAULT_THRESHOLD = 0.8;
    private static final double DEFAULT_DIMENSIONALITY = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public DistanceCategorizer() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(THRESHOLD, DEFAULT_THRESHOLD);
        this.numericParameters.put(DIMENSIONALITY, DEFAULT_DIMENSIONALITY);
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

    public double getValue(final ClusterSummary targetConcept,
                           final List<ClusterSummary> knownConcepts,
                           final Set<Integer> knownLabels) {

        final List<Sample> knownConceptsCentroids = knownConcepts
                .stream()
                .map(TypeConversion::toSample)
                .collect(Collectors.toList());

        final double bayesianErrorEstimation;

        if (knownConceptsCentroids.isEmpty()) {
            bayesianErrorEstimation = 1;
        } else {

            bayesianErrorEstimation = ProbabilityByEuclideanDistance.estimateError(
                    TypeConversion.toSample(targetConcept),
                    knownConceptsCentroids,
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
