package br.com.douglas444.pcf.impl.metacategorizer.lowlevel;

import br.com.douglas444.pcf.impl.estimators.ProbabilityByEuclideanDistance;
import br.com.douglas444.pcf.impl.commons.Oracle;
import br.com.douglas444.pcf.impl.commons.TypeConversion;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.*;
import java.util.stream.Collectors;

public class KMostOrLessInformativeCategorizer implements LowLevelCategorizer, Configurable {

    private static final String K = "K";
    private static final String DIMENSIONALITY = "Dimensionality";

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

        final List<Sample> targetConcepts = context.getClusterSummaries()
                .stream()
                .map(TypeConversion::toSample)
                .collect(Collectors.toList());

        final List<Sample> preLabeledSamples = TypeConversion.toPreLabeledSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels(),
                context.getIsPreLabeled());

        final List<Sample> samples = TypeConversion.toSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels(),
                context.getIsPreLabeled());

        final Comparator<Sample> comparator;
        if (context.getPredictedCategory() == Category.KNOWN) {

            comparator = Comparator.comparing(sample -> 1 - ProbabilityByEuclideanDistance.estimateError(
                    sample,
                    targetConcepts,
                    context.getKnownLabels(),
                    this.numericParameters.get(DIMENSIONALITY).intValue()));

        } else {

            comparator = Comparator.comparing(sample -> ProbabilityByEuclideanDistance.estimateError(
                    sample,
                    targetConcepts,
                    context.getKnownLabels(),
                    this.numericParameters.get(DIMENSIONALITY).intValue()));
        }

        samples.sort(comparator);

        final List<Sample> kSelected = samples.subList(0, this.getNumericParameters().get(K).intValue());
        return Oracle.categoryOf(kSelected, preLabeledSamples, context.getKnownLabels());

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
