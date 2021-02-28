package br.com.douglas444.bayesian_ee;

import java.util.Set;

public class Common {

    static double calculateNormalizedError(Set<Integer> knownLabels, double probability) {

        final double error = 1 - probability;
        final double maxError = 1 - 1 / (double) knownLabels.size();
        final double normalizedError = error / maxError;

        if (Double.isNaN(normalizedError)) {
            throw new IllegalStateException("Result of estimateBayesError is not a number");
        }

        return normalizedError;
    }

}
