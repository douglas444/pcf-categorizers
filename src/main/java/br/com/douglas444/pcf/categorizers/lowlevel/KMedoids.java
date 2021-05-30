package br.com.douglas444.pcf.categorizers.lowlevel;

import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.*;
import java.util.stream.Collectors;

public class KMedoids extends MajorityCategory implements Configurable {

    private static final String K = "K";
    private static final double DEFAULT_K = 1;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public KMedoids() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(K, DEFAULT_K);
    }

    @Override
    List<Sample> select(Context context, List<Sample> preLabeledSamples, List<Sample> unlabeledSamples) {

        final Sample centroid = TypeConversion.toSample(context.getPatternClusterSummary());

        final List<Sample> sortedSamples = unlabeledSamples
                .stream()
                .sorted(Comparator.comparing(sample -> sample.distance(centroid)))
                .collect(Collectors.toList());

        final int k = this.getNumericParameters().get(K).intValue();
        final List<Sample> selected = new ArrayList<>();
        if (!sortedSamples.isEmpty()) {
            if (sortedSamples.size() > k) {
                selected.addAll(sortedSamples.subList(0, k));
            } else {
                selected.addAll(sortedSamples);
            }
        }

        return selected;

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
