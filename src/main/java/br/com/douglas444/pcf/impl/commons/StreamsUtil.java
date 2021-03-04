package br.com.douglas444.pcf.impl.commons;

import br.com.douglas444.streams.datastructures.DynamicConfusionMatrixCompatible;
import br.com.douglas444.streams.processor.StreamsFileReader;
import br.com.douglas444.streams.processor.StreamsProcessor;
import br.com.douglas444.streams.processor.StreamsProcessorExecutor;

import java.io.IOException;
import java.util.Arrays;

public class StreamsUtil {

    public static boolean execute(final StreamsProcessor controller,
                                   final String[] filePaths,
                                   final StreamsProcessorExecutor executor,
                                   final int logInterval) {

        final String[] files = Arrays.stream(filePaths)
                .map(file -> file.replace(" ", ""))
                .filter(file -> !file.isEmpty())
                .toArray(String[]::new);

        final StreamsFileReader[] fileReaders = new StreamsFileReader[files.length];

        for (int i = 0; i < files.length; i++) {
            fileReaders[i] = new StreamsFileReader(",", FileUtil.getFileReader(files[i]));
        }

        try {
            if (executor.start(controller, logInterval, fileReaders)) {
                if (controller instanceof DynamicConfusionMatrixCompatible) {
                    System.out.println(
                            ((DynamicConfusionMatrixCompatible) controller)
                                    .getDynamicConfusionMatrix());
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
