package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

class OrderCancelTest {

    @Test
    void shouldCancelOrderInDraftStatus() {
        Order order = Order.draft(new CustomerId());

        order.cancel();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
        Assertions.assertThat(order.cancelledAt()).isNotNull();
        Assertions.assertThat(order.isCanceled()).isTrue();
    }

    @Test
    void shouldCancelOrderInPlacedStatus() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        order.cancel();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
        Assertions.assertThat(order.cancelledAt()).isNotNull();
        Assertions.assertThat(order.isCanceled()).isTrue();
    }

    @Test
    void shouldCancelOrderInPaidStatus() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();

        order.cancel();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
        Assertions.assertThat(order.cancelledAt()).isNotNull();
        Assertions.assertThat(order.isCanceled()).isTrue();
    }

    @Test
    void shouldCancelOrderInReadyStatus() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        order.markAsPaid();
        order.markAsReady();

        order.cancel();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
        Assertions.assertThat(order.cancelledAt()).isNotNull();
        Assertions.assertThat(order.isCanceled()).isTrue();
    }

    @Test
    void shouldNotCancelOrderAlreadyCanceled() {
        Order order = Order.draft(new CustomerId());
        order.cancel();
        OffsetDateTime originalCancelledAt = order.cancelledAt();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::cancel);

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
        Assertions.assertThat(order.cancelledAt()).isEqualTo(originalCancelledAt);
        Assertions.assertThat(order.isCanceled()).isTrue();
    }

    @Test
    void shouldReturnTrueForIsCanceledOnlyWhenStatusIsCanceled() {
        Order draftOrder = Order.draft(new CustomerId());
        Assertions.assertThat(draftOrder.isCanceled()).isFalse();

        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Assertions.assertThat(placedOrder.isCanceled()).isFalse();

        Order paidOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Assertions.assertThat(paidOrder.isCanceled()).isFalse();

        Order readyOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        readyOrder.markAsPaid();
        readyOrder.markAsReady();
        Assertions.assertThat(readyOrder.isCanceled()).isFalse();

        Order canceledOrder = Order.draft(new CustomerId());
        canceledOrder.cancel();
        Assertions.assertThat(canceledOrder.isCanceled()).isTrue();
    }
}
