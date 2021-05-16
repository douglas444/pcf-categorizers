package br.com.douglas444.pcf.categorizers.highlevel;

import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.pcf.categorizers.estimators.NextDenserNeighbour;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NDNCentroidRisk implements HighLevelCategorizer, Configurable {

    private static final String THRESHOLD = "Threshold";
    private static final String DIMENSIONALITY = "Dimensionality";

    private static final double DEFAULT_THRESHOLD = 0.8;
    private static final double DEFAULT_DIMENSIONALITY = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public NDNCentroidRisk() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(THRESHOLD, DEFAULT_THRESHOLD);
        this.numericParameters.put(DIMENSIONALITY, DEFAULT_DIMENSIONALITY);
    }

    @Override
    public Category categorize(final Context context) {

        final double bayesianErrorEstimation = getValue(
                TypeConversion.toSample(context.getPatternClusterSummary()),
                context.getClusterSummaries(),
                context.getKnownLabels());

        if (bayesianErrorEstimation > this.numericParameters.get(THRESHOLD)) {
            return Category.NOVELTY;
        } else {
            return Category.KNOWN;
        }
    }

    public double getValue(final Sample target,
                           final List<ClusterSummary> clusterSummaries,
                           final Set<Integer> knownLabels) {

        final double bayesianErrorEstimation;

        if (clusterSummaries.isEmpty()) {
            bayesianErrorEstimation = 1;
        } else {

            bayesianErrorEstimation = NextDenserNeighbour.estimateError(
                    target,
                    clusterSummaries,
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
