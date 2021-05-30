package br.com.douglas444.pcf.categorizers.lowlevel;

import br.com.douglas444.streams.algorithms.KMeans;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.*;
public class KSeeds extends MajorityCategory implements Configurable {

    private static final String K = "K";
    private static final String SEED = "Seed";
    private static final double DEFAULT_K = 1;
    private static final double DEFAULT_SEED = 0;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public KSeeds() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(K, DEFAULT_K);
        this.numericParameters.put(SEED, DEFAULT_SEED);
    }

    @Override
    List<Sample> select(Context context, List<Sample> preLabeledSamples, List<Sample> unlabeledSamples) {
        final Random random = new Random(this.numericParameters.get(SEED).intValue());
        return chooseCentroids(unlabeledSamples, this.numericParameters.get(K).intValue(), random);
    }

    @Override
    public HashMap<String, String> getNominalParameters() {
        return this.nominalParameters;
    }

    @Override
    public HashMap<String, Double> getNumericParameters() {
        return this.numericParameters;
    }


    private static List<Sample> chooseCentroids(final List<Sample> samples,
                                                final int k,
                                                final Random random) {

        final List<Sample> centroids = new ArrayList<>();

        for (int i = 0; i < k; ++i) {
            Sample centroid = selectNextCentroid(samples, centroids, random);
            centroids.add(centroid);
        }

        return centroids;

    }

    private static Sample selectNextCentroid(final List<Sample> samples,
                                             final List<Sample> centroids,
                                             final Random random) {

        final HashMap<Sample, Double> probabilityBySample = mapProbabilityBySample(samples, centroids);
        final List<Map.Entry<Sample, Double>> entries = new ArrayList<>(probabilityBySample.entrySet());
        final Iterator<Map.Entry<Sample, Double>> iterator = entries.iterator();

        double cumulativeProbability = 0;
        Sample selected = null;
        final double r = random.nextDouble();

        while (selected == null) {

            final Map.Entry<Sample, Double> entry = iterator.next();

            cumulativeProbability += entry.getValue();

            if (r <= cumulativeProbability || !iterator.hasNext()) {
                selected = entry.getKey();
            }

        }

        return selected;

    }

    private static HashMap<Sample, Double> mapProbabilityBySample(final List<Sample> samples,
                                                                  final List<Sample> centroids) {

        final HashMap<Sample, Double> probabilityBySample = new HashMap<>();

        samples.forEach(sample ->
                probabilityBySample.put(sample, Math.pow(KMeans.distanceToTheClosestCentroid(sample, centroids), 2)));

        final double sum = probabilityBySample.values().stream().reduce(0.0, Double::sum);
        probabilityBySample.replaceAll((sample, probability) -> probability / sum);

        return probabilityBySample;

    }
}
