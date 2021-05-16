package br.com.douglas444.pcf.categorizers.commons;

import java.util.Set;

public class Util {

    static double calculateNormalizedError(final Set<Integer> knownLabels, final double probability) {

        final double error = 1 - probability;
        final double maxError = 1 - 1 / (double) knownLabels.size();
        final double normalizedError = error / maxError;

        if (Double.isNaN(normalizedError)) {
            throw new IllegalStateException("Result of estimateBayesError is not a number");
        }

        return normalizedError;
    }

}
