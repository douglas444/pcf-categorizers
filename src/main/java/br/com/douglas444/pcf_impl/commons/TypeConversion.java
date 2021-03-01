package br.com.douglas444.pcf_impl.commons;

import br.com.douglas444.ndc.datastructures.Sample;
import br.ufu.facom.pcf.core.ClusterSummary;

import java.util.ArrayList;
import java.util.List;

public class TypeConversion {

    public static Sample toSample(final ClusterSummary clusterSummary) {
        return new Sample(clusterSummary.getCentroidAttributes(), clusterSummary.getLabel());
    }

    public static List<Sample> toSampleList(final List<double[]> samplesAttributes,
                                            final List<Integer> labels) {

        final List<Sample> samples = new ArrayList<>();

        for (int i = 0; i < samplesAttributes.size(); ++i) {
            samples.add(new Sample(samplesAttributes.get(i), labels.get(i)));
        }

        return samples;
    }

}
