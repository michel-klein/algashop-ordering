package com.algaworks.algashop.ordering.domain.valueobject;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record BirthDate(LocalDate value) {

    public BirthDate(LocalDate value) {
        Objects.requireNonNull(value);

        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public Integer age() {
        return (int) ChronoUnit.YEARS.between(value, LocalDate.now());
    }


    @Override
    public String toString() {
        return value.toString();
    }
}
