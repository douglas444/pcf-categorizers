package br.com.douglas444.util;

import br.com.douglas444.mltk.datastructure.Sample;
import br.ufu.facom.pcf.core.Category;

import java.util.List;
import java.util.Set;

public class Oracle {

    public static Category categoryOf(final List<Sample> samples, final Set<Integer> knownLabels) {

        final int sentence = samples.stream()
                .map(sample -> !knownLabels.contains(sample.getY()))
                .map(isNovel -> isNovel ? 1 : -1)
                .reduce(0, Integer::sum);

        if (sentence / (double) samples.size() >= 0) {
            return Category.NOVELTY;
        } else {
            return Category.KNOWN;
        }

    }

}
