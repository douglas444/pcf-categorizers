package br.com.douglas444.metacategorizer.lowlevel;

import br.com.douglas444.mltk.datastructure.Sample;
import br.com.douglas444.util.BayesianErrorEstimation;
import br.com.douglas444.util.Oracle;
import br.ufu.facom.pcf.core.*;

import java.util.*;
import java.util.stream.Collectors;

public class KMostOrLessInformativeCategorizer implements LowLevelCategorizer, Configurable {

    private static final String K = "K";
    private static final String DIMENSIONALITY = "DIMENSIONALITY";

    private static final double DEFAULT_K = 1;
    private static final double DEFAULT_DIMENSIONALITY = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public KMostOrLessInformativeCategorizer() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(K, DEFAULT_K);
        this.numericParameters.put(DIMENSIONALITY, DEFAULT_DIMENSIONALITY);
    }

    @Override
    public Category categorize(Context context) {

        if (context.getSamplesAttributes().isEmpty()) {
            throw new IllegalArgumentException();
        }

        final List<Sample> targetConcepts = context.getClusterSummaries()
                .stream()
                .map(ClusterSummary::calculateCentroidAttributes)
                .map(Sample::new)
                .collect(Collectors.toList());

        final Comparator<Sample> comparator;
        if (context.getPredictedCategory() == Category.KNOWN) {
            comparator = Comparator.comparing(sample -> BayesianErrorEstimation
                    .distanceProbability(sample, targetConcepts, context.getKnownLabels()));
        } else {
            comparator = Comparator.comparing(sample -> 1 - BayesianErrorEstimation
                    .distanceProbability(sample, targetConcepts, context.getKnownLabels()));
        }

        final List<Sample> samplesSortedByInformationGain = context.getSamplesAttributes()
                .stream()
                .map(Sample::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        final List<Sample> kSelected = samplesSortedByInformationGain
                .subList(0, this.getNumericParameters().get(K).intValue());

        return Oracle.categoryOf(kSelected, context.getKnownLabels());

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
