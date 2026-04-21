package com.algaworks.algashop.ordering.domain.model.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

class OrderMarkAsReadyTest {

    @Test
    public void shouldMarkAsReadyWhenOrderIsPaid() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.PAID);
        Assertions.assertThat(order.readyAt()).isNull();

        order.markAsReady();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.READY);
        Assertions.assertThat(order.readyAt()).isNotNull();
        Assertions.assertThat(order.readyAt()).isBeforeOrEqualTo(OffsetDateTime.now());
    }

    @Test
    public void shouldThrowExceptionWhenMarkingAsReadyFromNonPaidStatus() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).build();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.DRAFT);
        Assertions.assertThat(order.readyAt()).isNull();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> order.markAsReady());

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.DRAFT);
        Assertions.assertThat(order.readyAt()).isNull();
    }
}
