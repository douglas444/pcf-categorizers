package br.com.douglas444.pcf.categorizers.estimators;

import br.com.douglas444.pcf.categorizers.commons.Util;
import br.com.douglas444.streams.datastructures.Sample;

import java.util.*;

public class NextNeighbour {

    public static double estimateError(final Sample target,
                                       final List<Sample> centroids,
                                       final Set<Integer> knownLabels,
                                       final int dimensionality) {

        if (centroids.isEmpty()) {
            return 1;
        }

        final List<Sample> closestCentroids = new ArrayList<>();

        knownLabels.forEach((knownLabel) -> {

            final Optional<Sample> closestCentroid = centroids
                    .stream()
                    .filter(centroid -> centroid.getY().equals(knownLabel))
                    .min(Comparator.comparing((Sample sample) -> sample.distance(target)));

            closestCentroid.ifPresent(closestCentroids::add);

        });

        final double n = Math.pow(1.0 / closestCentroids
                .stream()
                .map(centroid -> centroid.distance(target))
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
                .map(centroid -> Math.pow(1.0 / centroid.distance(target), dimensionality))
                .reduce(0.0, Double::sum);

        final double probability = n / d;
        return Util.calculateNormalizedError(knownLabels, probability);
    }

}
