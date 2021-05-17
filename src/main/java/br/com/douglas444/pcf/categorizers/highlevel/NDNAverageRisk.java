package br.com.douglas444.pcf.categorizers.highlevel;

import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.pcf.categorizers.classifier.NextDenserNeighbour;
import br.com.douglas444.pcf.categorizers.commons.Util;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NDNAverageRisk implements HighLevelCategorizer, Configurable {

    private static final String THRESHOLD = "Threshold";
    private static final String DIMENSIONALITY = "Dimensionality";

    private static final double DEFAULT_THRESHOLD = 0.8;
    private static final double DEFAULT_DIMENSIONALITY = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public NDNAverageRisk() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(THRESHOLD, DEFAULT_THRESHOLD);
        this.numericParameters.put(DIMENSIONALITY, DEFAULT_DIMENSIONALITY);
    }

    @Override
    public Category categorize(final Context context) {

        final List<Sample> samples = TypeConversion.toSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels());

        final double bayesianErrorEstimation = getValue(
                samples,
                context.getClusterSummaries(),
                context.getKnownLabels());

        if (bayesianErrorEstimation > this.numericParameters.get(THRESHOLD)) {
            return Category.NOVELTY;
        } else {
            return Category.KNOWN;
        }
    }

    private double getValue(final List<Sample> targetSamples,
                            final List<ClusterSummary> clusterSummaries,
                            final Set<Integer> knownLabels) {

        if (targetSamples.isEmpty()) {
            throw new IllegalStateException("List targetSamples cannot be empty");
        }

        if (clusterSummaries.isEmpty()) {
            return 1;
        }

        double riskSum = 0;

        for (final Sample sample : targetSamples) {

            riskSum += 1 - NextDenserNeighbour.calculateProbability(
                    sample,
                    clusterSummaries,
                    knownLabels,
                    this.numericParameters.get(DIMENSIONALITY).intValue());
        }


        return Util.calculateNormalizedError(knownLabels,riskSum / (double) targetSamples.size());

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
