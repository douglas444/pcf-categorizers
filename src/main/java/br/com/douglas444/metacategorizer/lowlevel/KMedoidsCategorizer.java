package br.com.douglas444.metacategorizer.lowlevel;

import br.com.douglas444.commons.TypeConversion;
import br.com.douglas444.mltk.datastructure.Sample;
import br.com.douglas444.commons.Oracle;
import br.ufu.facom.pcf.core.Category;
import br.ufu.facom.pcf.core.Configurable;
import br.ufu.facom.pcf.core.Context;
import br.ufu.facom.pcf.core.LowLevelCategorizer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class KMedoidsCategorizer implements LowLevelCategorizer, Configurable {

    private static final String K = "K";
    private static final double DEFAULT_K = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public KMedoidsCategorizer() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(K, DEFAULT_K);
    }

    @Override
    public Category categorize(Context context) {

        final Sample centroid = TypeConversion.toSample(context.getPatternClusterSummary());

        final List<Sample> samples = TypeConversion.toSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels());

        final List<Sample> sortedSamples = samples
                .stream()
                .sorted(Comparator.comparing(sample -> sample.distance(centroid)))
                .collect(Collectors.toList());

        final List<Sample> kMedoids = sortedSamples.subList(0, this.numericParameters.get(K).intValue());
        return Oracle.categoryOf(kMedoids, context.getKnownLabels());

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
