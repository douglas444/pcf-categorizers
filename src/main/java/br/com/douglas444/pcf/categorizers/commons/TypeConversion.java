package br.com.douglas444.pcf.categorizers.commons;

import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.ClusterSummary;

import java.util.ArrayList;
import java.util.List;

public class TypeConversion {

    public static Sample toSample(final ClusterSummary clusterSummary) {
        return new Sample(clusterSummary.getCentroidAttributes(), clusterSummary.getLabel());
    }

    public static List<Sample> toSampleList(final double[][] samplesAttributes,
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

}
