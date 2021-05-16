package br.com.douglas444.pcf.categorizers.lowlevel;

import br.com.douglas444.pcf.categorizers.commons.Oracle;
import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.pcf.categorizers.estimators.NextNeighbour;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.Category;
import br.ufu.facom.pcf.core.Configurable;
import br.ufu.facom.pcf.core.Context;
import br.ufu.facom.pcf.core.LowLevelCategorizer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

        final List<Sample> samples = TypeConversion.toNotPreLabeledSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels(),
                context.getIsPreLabeled());

        final Comparator<Sample> comparator;
        if (context.getPredictedCategory() == Category.KNOWN) {

            comparator = Comparator.comparing(sample -> 1 - NextNeighbour.estimateError(
                    sample,
                    targetConcepts,
                    context.getKnownLabels(),
                    this.numericParameters.get(DIMENSIONALITY).intValue()));

        } else {

            comparator = Comparator.comparing(sample -> NextNeighbour.estimateError(
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
