package br.com.douglas444.pcf_impl.commons;

import br.com.douglas444.ndc.datastructures.Sample;
import br.ufu.facom.pcf.core.Category;

import java.util.List;
import java.util.Set;

public class Oracle {

    public static Category categoryOf(final List<Sample> samples,
                                      final List<Sample> preLabeledSamples,
                                      final Set<Integer> knownLabels) {

        int sentence = samples.stream()
                .map(sample -> !knownLabels.contains(labelOf(sample)))
                .map(isNovel -> isNovel ? 1 : -1)
                .reduce(0, Integer::sum);

        sentence += preLabeledSamples.stream()
                .map(sample -> !knownLabels.contains(labelOf(sample)))
                .map(isNovel -> isNovel ? 1 : -1)
                .reduce(0, Integer::sum);

        if (sentence / (double) samples.size() >= 0) {
            return Category.NOVELTY;
        } else {
            return Category.KNOWN;
        }

    }
    
    public static Integer labelOf(final Sample sample) {
        return sample.getY();
    }

}
