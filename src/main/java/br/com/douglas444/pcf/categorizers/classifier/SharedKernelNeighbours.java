package br.com.douglas444.pcf.categorizers.classifier;

import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.pcf.categorizers.commons.Util;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.ClusterSummary;

import java.util.*;

public class SharedKernelNeighbours {

    public static double calculateProbability(final ClusterSummary target,
                                              final List<ClusterSummary> clusterSummaries,
                                              final Set<Integer> knownLabels,
                                              final double factor) {

        if (clusterSummaries.isEmpty()) {
            return 0;
        }

        final List<ClusterSummary> closestClusterSummaries = new ArrayList<>();
        final Sample targetConceptCentroid = TypeConversion.toSample(target);

        knownLabels.forEach((knownLabel) -> {

            final Optional<ClusterSummary> optionalClosestSummary = clusterSummaries
                    .stream()
                    .filter(clusterSummary -> clusterSummary.getLabel().equals(knownLabel))
                    .min(Comparator.comparing((ClusterSummary clusterSummary) -> {
                        final Sample centroid = TypeConversion.toSample(clusterSummary);
                        return centroid.distance(targetConceptCentroid);
                    }));

            optionalClosestSummary.ifPresent(closestClusterSummaries::add);

        });

        final List<ClusterSummary> neighbourhood = new ArrayList<>(clusterSummaries);
        neighbourhood.removeAll(closestClusterSummaries);

        final double n = closestClusterSummaries
                .stream()
                .map(summary -> calculateSimilarity(summary, target, neighbourhood, factor))
                .max(Double::compare)
                .orElse(0.0);

        if (closestClusterSummaries.size() == 1) {
            return 0;
        }

        final double d = closestClusterSummaries
                .stream()
                .map(summary -> calculateSimilarity(summary, target, neighbourhood, factor))
                .reduce(0.0, Double::sum);

        final double probability;
        if (d == 0) {
            probability = 1 / (double) knownLabels.size();
        } else {
            probability = n / d;
        }

        return probability;
    }

    private static double calculateSimilarity(final ClusterSummary summary1,
                                              final ClusterSummary summary2,
                                              List<ClusterSummary> neighbourhood,
                                              final double factor) {

        neighbourhood = new ArrayList<>(neighbourhood);

        final List<ClusterSummary> nearestNeighborsSummary1 =
                new ArrayList<>(getKernelNeighbors(summary1, neighbourhood, factor));

        final List<ClusterSummary> nearestNeighborsSummary2 =
                new ArrayList<>(getKernelNeighbors(summary2, neighbourhood, factor));


        return intersection(nearestNeighborsSummary1, nearestNeighborsSummary2).size();

    }

    private static List<ClusterSummary> getKernelNeighbors(final ClusterSummary target,
                                                           List<ClusterSummary> clusterSummaries,
                                                           final double factor) {

        clusterSummaries = new ArrayList<>(clusterSummaries);

        final double standardDeviation = target.getStandardDeviation();
        final List<ClusterSummary> neighboursLevel1 = new ArrayList<>();
        final Sample targetCentroid = TypeConversion.toSample(target);

        for (ClusterSummary clusterSummary : clusterSummaries) {

            final double range = factor * Math.sqrt(standardDeviation)
                    + factor * Math.sqrt(clusterSummary.getStandardDeviation());

            final Sample clusterSummaryCentroid = TypeConversion.toSample(clusterSummary);

            if (clusterSummaryCentroid.distance(targetCentroid) <= range) {
                neighboursLevel1.add(clusterSummary);
            }

        }

        clusterSummaries.removeAll(neighboursLevel1);
        final List<ClusterSummary> neighboursLevel2 = new ArrayList<>();

        for (ClusterSummary neighbour : neighboursLevel1) {

            final double neighbourStandardDeviation = neighbour.getStandardDeviation();

            for (ClusterSummary clusterSummary : clusterSummaries) {

                final double range = Math.sqrt(neighbourStandardDeviation)
                        + Math.sqrt(clusterSummary.getStandardDeviation());

                final Sample clusterSummaryCentroid = TypeConversion.toSample(clusterSummary);

                if (clusterSummaryCentroid.distance(targetCentroid) <= range) {
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
        List<T> list = new ArrayList<>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

}
