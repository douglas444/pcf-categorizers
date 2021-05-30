package br.com.douglas444.pcf.categorizers.commons;

import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.Category;
import br.ufu.facom.pcf.core.ClusterSummary;
import br.ufu.facom.pcf.core.ResponseContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TypeConversion {

    public static Sample toSample(final ClusterSummary clusterSummary) {
        return new Sample(clusterSummary.getCentroidAttributes(), clusterSummary.getLabel());
    }

    public static int[] toIntArray(List<Integer> list){
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }

    public static List<Sample> toSampleList(final double[][] samplesAttributes,
                                            final int[] labels) {

        final List<Sample> samples = new ArrayList<>();
        for (int i = 0; i < samplesAttributes.length; ++i) {
            samples.add(new Sample(samplesAttributes[i], labels[i]));
        }
        return samples;
    }

    public static List<Sample> toNotPreLabeledSampleList(final double[][] samplesAttributes,
                                                         final int[] labels,
                                                         final boolean[] isPreLabeled) {

        final List<Sample> samples = new ArrayList<>();
        for (int i = 0; i < samplesAttributes.length; ++i) {
            if (isPreLabeled == null || !isPreLabeled[i]) {
                samples.add(new Sample(samplesAttributes[i], labels[i]));
            }
        }
        return samples;
    }

    public static List<Sample> toPreLabeledSampleList(final double[][] samplesAttributes,
                                                      final int[] labels,
                                                      final boolean[] isPreLabeled) {

        final List<Sample> samples = new ArrayList<>();

        if (isPreLabeled == null) {
            return samples;

        }
        for (int i = 0; i < samplesAttributes.length; ++i) {
            if (isPreLabeled[i]) {
                samples.add(new Sample(samplesAttributes[i], labels[i]));
            }
        }
        return samples;
    }

    public static ResponseContext toResponseContext(final List<Sample> selected,
                                                    final List<Sample> preLabeled,
                                                    final Category category) {

        final List<double[]> attributes = new ArrayList<>();
        final List<Integer> labels = new ArrayList<>();

        attributes.addAll(selected.stream().map(Sample::getX).collect(Collectors.toList()));
        attributes.addAll(preLabeled.stream().map(Sample::getX).collect(Collectors.toList()));

        labels.addAll(selected.stream().map(Sample::getY).collect(Collectors.toList()));
        labels.addAll(preLabeled.stream().map(Sample::getY).collect(Collectors.toList()));

        return new ResponseContext(category, attributes.toArray(new double[0][0]), TypeConversion.toIntArray(labels));
    }

}
