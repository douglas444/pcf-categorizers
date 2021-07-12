package br.com.douglas444.pcf.categorizers.classifier;

import br.com.douglas444.streams.datastructures.Sample;

import java.util.*;

public class NextNeighbour {

    public static double calculateProbability(final Sample target,
                                              final List<Sample> centroids,
                                              final Set<Integer> knownLabels,
                                              final int dimensionality) {

        if (centroids.isEmpty() || knownLabels.isEmpty()) {
            return 0;
        }

        if (centroids.size() == 1 || knownLabels.size() == 1) {
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
            return 1;
        }

        final double d = closestCentroids
                .stream()
                .map(centroid -> Math.pow(1.0 / centroid.distance(target), dimensionality))
                .reduce(0.0, Double::sum);

        return n / d;
    }

}
