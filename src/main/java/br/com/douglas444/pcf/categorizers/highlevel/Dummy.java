package br.com.douglas444.pcf.categorizers.highlevel;
import br.ufu.facom.pcf.core.*;

public class Dummy implements HighLevelCategorizer {

    @Override
    public Category categorize(final Context context) {

        if (context.getPredictedCategory() == Category.KNOWN) {
            return Category.NOVELTY;
        } else {
            return Category.KNOWN;
        }

    }


}
