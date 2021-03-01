package br.com.douglas444.pcf_impl.metacategorizer.lowlevel;

import br.com.douglas444.pcf_impl.commons.TypeConversion;
import br.com.douglas444.pcf_impl.commons.Oracle;
import br.com.douglas444.ndc.datastructures.Sample;
import br.ufu.facom.pcf.core.Category;
import br.ufu.facom.pcf.core.Configurable;
import br.ufu.facom.pcf.core.Context;
import br.ufu.facom.pcf.core.LowLevelCategorizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

        final List<Sample> candidates = TypeConversion.toSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels());

        final List<Sample> kSelected = new ArrayList<>();

        for (int i = 0; i < this.numericParameters.get(K).intValue(); ++i) {
            final Sample selected = candidates.remove(random.nextInt(candidates.size()));
            kSelected.add(selected);
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