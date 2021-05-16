package br.com.douglas444.pcf.categorizers.estimators;

import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.pcf.categorizers.commons.Util;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.ClusterSummary;

import java.util.*;

public class NextDenserNeighbour {

    public static double estimateError(final Sample sample,
                                       final List<ClusterSummary> clusterSummaries,
                                       final Set<Integer> knownLabels,
                                       final int dimensionality) {

        if (clusterSummaries.isEmpty()) {
            return 1;
        }

        final List<ClusterSummary> closestClusterSummaries = new ArrayList<>();

        knownLabels.forEach((knownLabel) -> {

            final Optional<ClusterSummary> optionalClosestSummary = clusterSummaries
                    .stream()
                    .filter(summary -> summary.getLabel().equals(knownLabel))
                    .min(Comparator.comparing((ClusterSummary clusterSummary) -> {
                        final Sample centroid = TypeConversion.toSample(clusterSummary);
                        return clusterSummary.getStandardDeviation() * centroid.distance(sample);
                    }));

            optionalClosestSummary.ifPresent(closestClusterSummaries::add);

        });

        final double n = closestClusterSummaries
                .stream()
                .map(summary -> {
                    final Sample centroid = TypeConversion.toSample(summary);
                    return summary.getStandardDeviation() * centroid.distance(sample);
                })
                .min(Double::compare)
                .map(x -> Math.pow(1 / x, dimensionality))
                .orElse(0.0);

        if (Double.isInfinite(n)) {
            return 0;
        }

        if (closestClusterSummaries.size() == 1) {
            return 1;
        }

        final double d = closestClusterSummaries
                .stream()
                .map(summary -> {
                    final Sample centroid = TypeConversion.toSample(summary);
                    return summary.getStandardDeviation() * centroid.distance(sample);
                })
                .map(x -> Math.pow(1 / x, dimensionality))
                .reduce(0.0, Double::sum);

        final double probability = n / d;
        return Util.calculateNormalizedError(knownLabels, probability);
    }

}
