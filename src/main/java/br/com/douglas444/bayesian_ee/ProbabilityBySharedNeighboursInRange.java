package br.com.douglas444.bayesian_ee;

import br.com.douglas444.commons.TypeConversion;
import br.com.douglas444.mltk.datastructure.Sample;
import br.ufu.facom.pcf.core.ClusterSummary;

import java.util.*;

public class ProbabilityBySharedNeighboursInRange {

    public static double estimateError(final ClusterSummary targetConcept,
                                       final List<ClusterSummary> knownConcepts,
                                       final Set<Integer> knownLabels,
                                       final double factor) {

        if (knownConcepts.isEmpty()) {
            return 1;
        }

        final List<ClusterSummary> closestClusterSummaries = new ArrayList<>();
        final Sample targetConceptCentroid = TypeConversion.toSample(targetConcept);

        knownLabels.forEach((knownLabel) -> {

            final Optional<ClusterSummary> optionalClosestSummary = knownConcepts
                    .stream()
                    .filter(clusterSummary -> clusterSummary.getLabel().equals(knownLabel))
                    .min(Comparator.comparing((ClusterSummary clusterSummary) -> {
                        final Sample centroid = TypeConversion.toSample(clusterSummary);
                        return centroid.distance(targetConceptCentroid);
                    }));

            optionalClosestSummary.ifPresent(closestClusterSummaries::add);

        });

        final List<ClusterSummary> neighbourhood = new ArrayList<>(knownConcepts);
        neighbourhood.removeAll(closestClusterSummaries);

        final double n = closestClusterSummaries
                .stream()
                .map(summary -> calculateSimilarity(summary, targetConcept, neighbourhood, factor))
                .max(Double::compare)
                .orElse(0.0);

        if (closestClusterSummaries.size() == 1) {
            return 1;
        }

        final double d = closestClusterSummaries
                .stream()
                .map(summary -> calculateSimilarity(summary, targetConcept, neighbourhood, factor))
                .reduce(0.0, Double::sum);

        final double probability;
        if (d == 0) {
            probability = 1 / (double) knownLabels.size();
        } else {
            probability = n / d;
        }

        return Common.calculateNormalizedError(knownLabels, probability);
    }

    private static double calculateSimilarity(final ClusterSummary summary1,
                                              final ClusterSummary summary2,
                                              List<ClusterSummary> neighbourhood,
                                              final double factor) {

        neighbourhood = new ArrayList<>(neighbourhood);

        final List<ClusterSummary> nearestNeighborsSummary1 =
                new ArrayList<>(getNearestNeighbors(summary1, neighbourhood, factor));

        final List<ClusterSummary> nearestNeighborsSummary2 =
                new ArrayList<>(getNearestNeighbors(summary2, neighbourhood, factor));


        return intersection(nearestNeighborsSummary1, nearestNeighborsSummary2).size();

    }

    private static List<ClusterSummary> getNearestNeighbors(final ClusterSummary targetClusterSummary,
                                                            List<ClusterSummary> clusterSummaries,
                                                            final double factor) {

        clusterSummaries = new ArrayList<>(clusterSummaries);

        final double standardDeviation = targetClusterSummary.getStandardDeviation();
        final List<ClusterSummary> neighboursLevel1 = new ArrayList<>();
        final Sample targetClusterSummaryCentroid = TypeConversion.toSample(targetClusterSummary);

        for (ClusterSummary clusterSummary : clusterSummaries) {

            final double range = factor * Math.sqrt(standardDeviation)
                    + factor * Math.sqrt(clusterSummary.getStandardDeviation());

            final Sample clusterSummaryCentroid = TypeConversion.toSample(clusterSummary);

            if (clusterSummaryCentroid.distance(targetClusterSummaryCentroid) <= range) {
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

                if (clusterSummaryCentroid.distance(targetClusterSummaryCentroid) <= range) {
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
