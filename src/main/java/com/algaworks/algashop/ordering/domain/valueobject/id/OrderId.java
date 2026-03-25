package com.algaworks.algashop.ordering.domain.valueobject.id;

import com.algaworks.algashop.ordering.domain.utility.IDGenerator;
import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record OrderId(
        TSID value
) {
    public OrderId {
        Objects.requireNonNull(value);
    }

    public OrderId(Long value) {
        this(TSID.from(value));
    }

    public OrderId(String value) {
        this(TSID.from(value));
    }

    public OrderId() {
        this(IDGenerator.generateTSID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
