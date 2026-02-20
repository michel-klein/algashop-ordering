package com.algaworks.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

public class IDGenerator {
    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator
            = Generators.timeBasedEpochRandomGenerator();


    private IDGenerator() {
    }

    public static UUID generateTimeBasedUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }
}
