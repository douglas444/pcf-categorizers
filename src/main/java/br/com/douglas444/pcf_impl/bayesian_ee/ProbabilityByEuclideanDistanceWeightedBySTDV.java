package br.com.douglas444.pcf_impl.bayesian_ee;

import br.com.douglas444.pcf_impl.commons.TypeConversion;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.ClusterSummary;

import java.util.*;

public class ProbabilityByEuclideanDistanceWeightedBySTDV {

    public static double estimateError(final ClusterSummary targetConcept,
                                       final List<ClusterSummary> knownConcepts,
                                       final Set<Integer> knownLabels,
                                       final int dimensionality) {

        if (knownConcepts.isEmpty()) {
            return 1;
        }

        final List<ClusterSummary> closestClusterSummaries = new ArrayList<>();
        final Sample targetConceptCentroid = TypeConversion.toSample(targetConcept);

        knownLabels.forEach((knownLabel) -> {

            final Optional<ClusterSummary> optionalClosestSummary = knownConcepts
                    .stream()
                    .filter(summary -> summary.getLabel().equals(knownLabel))
                    .min(Comparator.comparing((ClusterSummary clusterSummary) -> {
                        final Sample centroid = TypeConversion.toSample(clusterSummary);
                        return clusterSummary.getStandardDeviation()
                                * centroid.distance(targetConceptCentroid);
                    }));

            optionalClosestSummary.ifPresent(closestClusterSummaries::add);

        });

        final double n = closestClusterSummaries
                .stream()
                .map(summary -> {
                    final Sample centroid = TypeConversion.toSample(summary);
                    return summary.getStandardDeviation() * centroid.distance(targetConceptCentroid);
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
                    return summary.getStandardDeviation() * centroid.distance(targetConceptCentroid);
                })
                .map(x -> Math.pow(1 / x, dimensionality))
                .reduce(0.0, Double::sum);

        final double probability = n / d;
        return Common.calculateNormalizedError(knownLabels, probability);
    }

}
