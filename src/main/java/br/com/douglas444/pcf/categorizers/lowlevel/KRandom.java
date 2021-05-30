package br.com.douglas444.pcf.categorizers.lowlevel;

import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class KRandom extends MajorityCategory implements Configurable {

    private static final String K = "K";
    private static final String SEED = "Seed";
    private static final double DEFAULT_K = 1;
    private static final double DEFAULT_SEED = 0;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public KRandom() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(K, DEFAULT_K);
        this.numericParameters.put(SEED, DEFAULT_SEED);
    }

    @Override
    List<Sample> select(Context context, List<Sample> preLabeledSamples, List<Sample> unlabeledSamples) {

        final List<Sample> selected = new ArrayList<>();
        final Random random = new Random(this.numericParameters.get(SEED).intValue());

        final int k = this.getNumericParameters().get(K).intValue();
        if (unlabeledSamples.size() < k) {
            selected.addAll(unlabeledSamples);
        } else {
            for (int i = 0; i < this.numericParameters.get(K).intValue(); ++i) {
                selected.add(unlabeledSamples.remove(random.nextInt(unlabeledSamples.size())));
            }
        }

        return selected;
    }

    @Override
    public HashMap<String, String> getNominalParameters() {
        return this.nominalParameters;
    }

    @Override
    public HashMap<String, Double> getNumericParameters() {
        return this.numericParameters;
    }
}