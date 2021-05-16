package br.com.douglas444.pcf.categorizers.highlevel;

import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.pcf.categorizers.estimators.NextNeighbour;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NNAverageRisk implements HighLevelCategorizer, Configurable {

    private static final String THRESHOLD = "Threshold";
    private static final String DIMENSIONALITY = "Dimensionality";

    private static final double DEFAULT_THRESHOLD = 0.8;
    private static final double DEFAULT_DIMENSIONALITY = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public NNAverageRisk() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(THRESHOLD, DEFAULT_THRESHOLD);
        this.numericParameters.put(DIMENSIONALITY, DEFAULT_DIMENSIONALITY);
    }

    @Override
    public Category categorize(Context context) {

        final double bayesianErrorEstimation = getValue(
                TypeConversion.toSampleList(context.getSamplesAttributes(), context.getSamplesLabels()),
                context.getClusterSummaries(),
                context.getKnownLabels());

        if (bayesianErrorEstimation > this.numericParameters.get(THRESHOLD)) {
            return Category.NOVELTY;
        } else {
            return Category.KNOWN;
        }
    }

    public double getValue(final List<Sample> targetSamples,
                           final List<ClusterSummary> clusterSummaries,
                           final Set<Integer> knownLabels) {

        if (targetSamples.isEmpty()) {
            throw new IllegalStateException("List targetSamples cannot be empty");
        }

        if (clusterSummaries.isEmpty()) {
            return 1;
        }

        final List<Sample> knownConceptsCentroids = clusterSummaries
                .stream()
                .map(TypeConversion::toSample)
                .collect(Collectors.toList());

        double riskSum = 0;

        for (Sample sample : targetSamples) {

            riskSum += NextNeighbour.estimateError(
                    sample,
                    knownConceptsCentroids,
                    knownLabels,
                    this.numericParameters.get(DIMENSIONALITY).intValue());
        }


        return riskSum / (double) targetSamples.size();
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
