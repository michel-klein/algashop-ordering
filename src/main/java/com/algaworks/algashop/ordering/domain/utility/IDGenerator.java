package com.algaworks.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import io.hypersistence.tsid.TSID;

import java.util.UUID;

public class IDGenerator {
    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator
            = Generators.timeBasedEpochRandomGenerator();

    private static final TSID.Factory tsidFactory = TSID.Factory.INSTANCE;


    private IDGenerator() {
    }

    public static UUID generateTimeBasedUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }

    /* ENVIRONMENT VARIABLES
    *  TSID_NODE
    *  TSID_NODE_COUNT
    */
    public static TSID generateTSID() {
        return tsidFactory.generate();
    }
}
