package br.com.douglas444.pcf_impl.bayesian_ee;

import br.com.douglas444.ndc.datastructures.Sample;

import java.util.*;

public class ProbabilityByEuclideanDistance {

    public static double estimateError(final Sample targetConceptCentroid,
                                       final List<Sample> knownConceptsCentroids,
                                       final Set<Integer> knownLabels,
                                       final int dimensionality) {

        if (knownConceptsCentroids.isEmpty()) {
            return 1;
        }

        final List<Sample> closestCentroids = new ArrayList<>();

        knownLabels.forEach((knownLabel) -> {

            final Optional<Sample> optionalClosestCentroid = knownConceptsCentroids
                    .stream()
                    .filter(centroid -> centroid.getY().equals(knownLabel))
                    .min(Comparator.comparing((Sample sample) -> sample.distance(targetConceptCentroid)));

            optionalClosestCentroid.ifPresent(closestCentroids::add);

        });

        final double n = Math.pow(1.0 / closestCentroids
                .stream()
                .map(centroid -> centroid.distance(targetConceptCentroid))
                .min(Double::compare)
                .orElse(0.0), dimensionality);

        if (Double.isInfinite(n)) {
            return 0;
        }

        if (closestCentroids.size() == 1) {
            return 1;
        }

        final double d = closestCentroids
                .stream()
                .map(centroid -> Math.pow(1.0 / centroid.distance(targetConceptCentroid), dimensionality))
                .reduce(0.0, Double::sum);

        final double probability = n / d;
        return Common.calculateNormalizedError(knownLabels, probability);
    }

}
