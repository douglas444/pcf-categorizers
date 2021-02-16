package br.com.douglas444.metacategorizer.lowlevel;

import br.com.douglas444.mltk.datastructure.Sample;
import br.com.douglas444.util.Oracle;
import br.ufu.facom.pcf.core.Category;
import br.ufu.facom.pcf.core.Configurable;
import br.ufu.facom.pcf.core.Context;
import br.ufu.facom.pcf.core.LowLevelCategorizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KRandomCategorizer implements LowLevelCategorizer, Configurable {

    private static final String K = "K";
    private static final String SEED = "Seed";
    private static final double DEFAULT_K = 1;
    private static final double DEFAULT_SEED = 0;

    private final HashMap<String, String> nominalParameters;
    private final HashMap<String, Double> numericParameters;

    public KRandomCategorizer() {
        this.nominalParameters = new HashMap<>();
        this.numericParameters = new HashMap<>();
        this.numericParameters.put(K, DEFAULT_K);
        this.numericParameters.put(SEED, DEFAULT_SEED);
    }

    @Override
    public Category categorize(Context context) {

        final Random random = new Random(this.numericParameters.get(SEED).intValue());

        final List<Sample> candidates = context.getSamplesAttributes().stream().map(Sample::new)
                .collect(Collectors.toList());

        final List<Sample> kSelected = new ArrayList<>();

        for (int i = 0; i < this.numericParameters.get(K).intValue(); ++i) {
            kSelected.add(candidates.remove(random.nextInt() % candidates.size()));
        }

        return Oracle.categoryOf(kSelected, context.getKnownLabels());


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