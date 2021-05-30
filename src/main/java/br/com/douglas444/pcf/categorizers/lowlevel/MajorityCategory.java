package br.com.douglas444.pcf.categorizers.lowlevel;

import br.com.douglas444.pcf.categorizers.commons.Oracle;
import br.com.douglas444.pcf.categorizers.commons.TypeConversion;
import br.com.douglas444.streams.datastructures.Sample;
import br.ufu.facom.pcf.core.*;

import java.util.List;

public abstract class MajorityCategory implements LowLevelCategorizer {

    @Override
    public ResponseContext categorize(Context context) {

        final List<Sample> preLabeledSamples = TypeConversion.toPreLabeledSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels(),
                context.getIsPreLabeled());

        final List<Sample> unlabeledSamples = TypeConversion.toNotPreLabeledSampleList(
                context.getSamplesAttributes(),
                context.getSamplesLabels(),
                context.getIsPreLabeled());

        final List<Sample> selected = select(context, preLabeledSamples, unlabeledSamples);

        final Category category = Oracle.categoryOf(selected, preLabeledSamples, context.getKnownLabels());
        return TypeConversion.toResponseContext(selected, preLabeledSamples, category);

    }

    abstract List<Sample> select(final Context context, final List<Sample> preLabeledSamples, final List<Sample> unlabeledSamples);

}
