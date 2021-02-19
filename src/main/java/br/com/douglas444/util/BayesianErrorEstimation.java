package br.com.douglas444.util;

import br.com.douglas444.mltk.datastructure.Sample;
import br.ufu.facom.pcf.core.ClusterSummary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class BayesianErrorEstimation {

    public static double distanceProbability(final Sample target, final List<Sample> targetConcepts,
                                             final Set<Integer> knownLabels) {

        if (targetConcepts.isEmpty()) {
            return 1;
        }

        final List<Sample> closestCentroids = new ArrayList<>();

        knownLabels.forEach((knownLabel) -> {

            final Sample closestCentroid = targetConcepts
                    .stream()
                    .filter(centroid -> centroid.getY().equals(knownLabel))
                    .min(Comparator.comparing((Sample sample) -> sample.distance(target)))
                    .orElse(targetConcepts.get(0));

            closestCentroids.add(closestCentroid);

        });

        final int dimensionality = 1;

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
        return calculateNormalizedError(knownLabels, probability);
    }

    public static double weightedDistanceProbability(final ClusterSummary target,
                                                     final List<ClusterSummary> targetSummaries,
                                                     final Set<Integer> knownLabels) {

        if (targetSummaries.isEmpty()) {
            return 1;
        }

        final List<ClusterSummary> closestClusterSummaries = new ArrayList<>();
        final Sample targetCentroid = new Sample(target.calculateCentroidAttributes(), null);

        knownLabels.forEach((knownLabel) -> {

            final ClusterSummary closestClusterSummary = targetSummaries
                    .stream()
                    .filter(summary -> summary.getLabel().equals(knownLabel))
                    .min(Comparator.comparing((ClusterSummary clusterSummary) -> {
                        final Sample centroid = new Sample(clusterSummary.calculateCentroidAttributes(), null);
                        return clusterSummary.calculateStandardDeviation() * centroid.distance(targetCentroid);
                    }))
                    .orElse(targetSummaries.get(0));

            closestClusterSummaries.add(closestClusterSummary);

        });

        final int dimensionality = 1;

        final double n = closestClusterSummaries
                .stream()
                .map(summary -> {
                    final Sample centroid = new Sample(summary.calculateCentroidAttributes(), null);
                    return summary.calculateStandardDeviation() * centroid.distance(targetCentroid);
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
                    final Sample centroid = new Sample(summary.calculateCentroidAttributes(), null);
                    return summary.calculateStandardDeviation() * centroid.distance(targetCentroid);
                })
                .map(x -> Math.pow(1 / x, dimensionality))
                .reduce(0.0, Double::sum);

        final double probability = n / d;
        return calculateNormalizedError(knownLabels, probability);
    }

    public static double sharedNeighboursProbability(final ClusterSummary target,
                                                     final List<ClusterSummary> targetClusterSummaries,
                                                     final Set<Integer> knownLabels,
                                                     final double factor) {

        if (targetClusterSummaries.isEmpty()) {
            return 1;
        }

        final List<ClusterSummary> closestClusterSummaries = new ArrayList<>();

        knownLabels.forEach((knownLabel) -> {

            final ClusterSummary closestClusterSummary = targetClusterSummaries
                    .stream()
                    .filter(clusterSummary -> clusterSummary.getLabel().equals(knownLabel))
                    .min(Comparator.comparing((ClusterSummary clusterSummary) -> {
                        final Sample centroid = new Sample(clusterSummary.calculateCentroidAttributes(), null);
                        final Sample targetCentroid = new Sample(target.calculateCentroidAttributes(), null);
                        return centroid.distance(targetCentroid);
                    }))
                    .orElse(targetClusterSummaries.get(0));

            closestClusterSummaries.add(closestClusterSummary);

        });

        final List<ClusterSummary> candidates = new ArrayList<>(targetClusterSummaries);
        candidates.removeAll(closestClusterSummaries);

        final double n = closestClusterSummaries
                .stream()
                .map(summary -> calculateSimilarity(summary, target, candidates, factor))
                .max(Double::compare)
                .orElse(0.0);

        if (closestClusterSummaries.size() == 1) {
            return 1;
        }

        final double d = closestClusterSummaries
                .stream()
                .map(summary -> calculateSimilarity(summary, target, candidates, factor))
                .reduce(0.0, Double::sum);

        final double probability;
        if (d == 0) {
            probability = 1 / (double) knownLabels.size();
        } else {
            probability = n / d;
        }

        return calculateNormalizedError(knownLabels, probability);
    }

    private static double calculateNormalizedError(Set<Integer> knownLabels, double probability) {
        final double error = 1 - probability;
        final double maxError = 1 - 1 / (double) knownLabels.size();
        final double normalizedError = error / maxError;

        if (Double.isNaN(normalizedError)) {
            throw new IllegalStateException("Result of estimateBayesError is not a number");
        }

        return normalizedError;
    }

    private static double calculateSimilarity(final ClusterSummary summary1, ClusterSummary summary2,
                                              final List<ClusterSummary> summaries, final double factor) {

        final List<ClusterSummary> samplesFiltered = new ArrayList<>(summaries);

        final List<ClusterSummary> nearestNeighborsSummary1 =
                new ArrayList<>(getNearestNeighbors(summary1, samplesFiltered, factor));

        final List<ClusterSummary> nearestNeighborsSummary2 =
                new ArrayList<>(getNearestNeighbors(summary2, samplesFiltered, factor));


        return intersection(nearestNeighborsSummary1,
                nearestNeighborsSummary2).size();

    }

    private static List<ClusterSummary> getNearestNeighbors(final ClusterSummary summary,
                                                            List<ClusterSummary> summaries,
                                                            final double factor) {

        summaries = new ArrayList<>(summaries);

        final double standardDeviation = summary.calculateStandardDeviation();

        final List<ClusterSummary> neighboursLevel1 = new ArrayList<>();
        for (ClusterSummary clusterSummary : summaries) {

            final double range = factor * Math.sqrt(standardDeviation)
                    + factor * Math.sqrt(clusterSummary.calculateStandardDeviation());

            final Sample clusterSummaryCentroid = new Sample(clusterSummary.calculateCentroidAttributes(), null);
            final Sample summaryCentroid = new Sample(summary.calculateCentroidAttributes(), null);

            if (clusterSummaryCentroid.distance(summaryCentroid) <= range) {
                neighboursLevel1.add(clusterSummary);
            }

        }

        summaries.removeAll(neighboursLevel1);
        final List<ClusterSummary> neighboursLevel2 = new ArrayList<>();

        for (ClusterSummary neighbour : neighboursLevel1) {

            final double neighbourStandardDeviation = neighbour.calculateStandardDeviation();

            for (ClusterSummary clusterSummary : summaries) {

                final double range = Math.sqrt(neighbourStandardDeviation) + Math.sqrt(clusterSummary.calculateStandardDeviation());

                final Sample clusterSummaryCentroid = new Sample(clusterSummary.calculateCentroidAttributes(), null);
                final Sample summaryCentroid = new Sample(summary.calculateCentroidAttributes(), null);
                if (clusterSummaryCentroid.distance(summaryCentroid) <= range) {
                    neighboursLevel2.add(clusterSummary);
                }

            }

        }

        final List<ClusterSummary> neighbours = new ArrayList<>();
        neighbours.addAll(neighboursLevel1);
        neighbours.addAll(neighboursLevel2);

        return neighbours;
    }

    static private <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}
