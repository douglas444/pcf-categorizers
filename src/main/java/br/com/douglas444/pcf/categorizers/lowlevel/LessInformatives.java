package br.com.douglas444.pcf.categorizers.lowlevel;

import br.com.douglas444.pcf.categorizers.commons.Oracle;
import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.pcf.categorizers.classifier.NextNeighbour;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.Category;
import br.ufu.facom.pcf.core.Configurable;
import br.ufu.facom.pcf.core.Context;
import br.ufu.facom.pcf.core.LowLevelCategorizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LessInformatives implements LowLevelCategorizer, Configurable {

    private static final String K = "K";
    private static final String DIMENSIONALITY = "Dimensionality";

    private static final double DEFAULT_K = 1;
    private static final double DEFAULT_DIMENSIONALITY = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public LessInformatives() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(K, DEFAULT_K);
        this.numericParameters.put(DIMENSIONALITY, DEFAULT_DIMENSIONALITY);
    }

    @Override
    public Category categorize(Context context) {

        final List<Sample> centroids = context.getClusterSummaries()
                .stream()
                .map(TypeConversion::toSample)
                .collect(Collectors.toList());

        final List<Sample> preLabeledSamples = TypeConversion.toPreLabeledSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels(),
                context.getIsPreLabeled());

        final List<Sample> unlabeledSamples = TypeConversion.toNotPreLabeledSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels(),
                context.getIsPreLabeled());

        final int k = this.getNumericParameters().get(K).intValue();
        if (unlabeledSamples.size() < 2 * k) {
            return Oracle.categoryOf(unlabeledSamples, preLabeledSamples, context.getKnownLabels());
        }

        final Comparator<Sample> comparator;

        comparator = Comparator.comparing(sample -> NextNeighbour.calculateProbability(
            sample,
            centroids,
            context.getKnownLabels(),
            this.numericParameters.get(DIMENSIONALITY).intValue()));

        unlabeledSamples.sort(comparator);

        final List<Sample> selected = new ArrayList<>();
        selected.addAll(unlabeledSamples.subList(0, k));
        selected.addAll(unlabeledSamples.subList(unlabeledSamples.size() - k, unlabeledSamples.size()));

        final int middle = unlabeledSamples.size() / 2;
        selected.addAll(unlabeledSamples.subList(middle, middle + k));
        selected.addAll(unlabeledSamples.subList(middle - k, middle));

        return Oracle.categoryOf(selected, preLabeledSamples, context.getKnownLabels());

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
