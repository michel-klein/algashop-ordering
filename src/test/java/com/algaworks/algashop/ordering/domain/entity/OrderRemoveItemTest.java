package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderRemoveItemTest {

    @Test
    public void shouldRemoveItemSuccessfully() {
        Order order = Order.draft(new CustomerId());

        order.addItem(
                ProductTestDataBuilder.aProductAltMousePad().build(),
                new Quantity(2)
        );
        order.addItem(
                ProductTestDataBuilder.aProductAltRamMemory().build(),
                new Quantity(1)
        );

        Assertions.assertThat(order.items()).hasSize(2);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("400"));
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(3));

        OrderItem orderItemToRemove = order.items().iterator().next();
        order.removeItem(orderItemToRemove.id());

        Assertions.assertThat(order.items()).hasSize(1);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("200"));
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(1));
    }

    @Test
    public void shouldThrowExceptionWhenRemovingNonExistentItem() {
        Order order = Order.draft(new CustomerId());

        OrderItemId nonExistentId = new OrderItemId();

        Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(nonExistentId));
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRemoveFromNonEditableOrder() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        OrderItemId someId = new OrderItemId(); // Assuming there's an item, but since it's placed, it should throw before checking item

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.removeItem(someId));
    }
}
