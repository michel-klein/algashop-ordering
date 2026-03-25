package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class OrderChangingTest {

    // ========== SECTION 1: Draft Order Modifications ==========

    @Test
    void givenDraftOrder_whenAddItem_shouldAllow() {
        Order order = Order.draft(new CustomerId());
        Product product = ProductTestDataBuilder.aProductAltMousePad().build();

        order.addItem(product, new Quantity(2));

        Assertions.assertThat(order.items()).hasSize(1);
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(2));
    }

    @Test
    void givenDraftOrder_whenChangeShipping_shouldAllow() {
        Order order = Order.draft(new CustomerId());
        Shipping shipping = OrderTestDataBuilder.aShipping();

        order.changeShipping(shipping);

        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
    }

    @Test
    void givenDraftOrder_whenChangeBilling_shouldAllow() {
        Order order = Order.draft(new CustomerId());
        Billing billing = OrderTestDataBuilder.aBilling();

        order.changeBilling(billing);

        Assertions.assertThat(order.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrder_whenChangePaymentMethod_shouldAllow() {
        Order order = Order.draft(new CustomerId());

        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);

        Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void givenDraftOrder_whenMultipleModifications_shouldAllow() {
        Order order = Order.draft(new CustomerId());
        Product product = ProductTestDataBuilder.aProductAltMousePad().build();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Billing billing = OrderTestDataBuilder.aBilling();

        order.addItem(product, new Quantity(1));
        order.changeShipping(shipping);
        order.changeBilling(billing);
        order.changePaymentMethod(PaymentMethod.GATEWAY_BALANCE);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.items()).hasSize(1),
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping),
                o -> Assertions.assertThat(o.billing()).isEqualTo(billing),
                o -> Assertions.assertThat(o.paymentMethod()).isEqualTo(PaymentMethod.GATEWAY_BALANCE)
        );
    }

    // ========== SECTION 2: Modifications on Non-Draft Orders ==========

    @Test
    void givenPlacedOrder_whenTryToAddItem_shouldThrowOrderCannotBeEditedException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Product product = ProductTestDataBuilder.aProductAltMousePad().build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.addItem(product, new Quantity(1)));
    }

    @Test
    void givenPlacedOrder_whenTryToChangeShipping_shouldThrowOrderCannotBeEditedException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeShipping(shipping));
    }

    @Test
    void givenPlacedOrder_whenTryToChangeBilling_shouldThrowOrderCannotBeEditedException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Billing billing = OrderTestDataBuilder.aBilling();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeBilling(billing));
    }

    @Test
    void givenPlacedOrder_whenTryToChangePaymentMethod_shouldThrowOrderCannotBeEditedException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changePaymentMethod(PaymentMethod.GATEWAY_BALANCE));
    }

    @Test
    void givenPaidOrder_whenTryToAddItem_shouldThrowOrderCannotBeEditedException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Product product = ProductTestDataBuilder.aProductAltMousePad().build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.addItem(product, new Quantity(1)));
    }

    @Test
    void givenPaidOrder_whenTryToChangeShipping_shouldThrowOrderCannotBeEditedException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeShipping(shipping));
    }

    @Test
    void givenPaidOrder_whenTryToChangeBilling_shouldThrowOrderCannotBeEditedException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Billing billing = OrderTestDataBuilder.aBilling();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeBilling(billing));
    }

    @Test
    void givenPaidOrder_whenTryToChangePaymentMethod_shouldThrowOrderCannotBeEditedException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changePaymentMethod(PaymentMethod.GATEWAY_BALANCE));
    }

    // ========== SECTION 3: Negative Tests - Status Transition and Editing ==========

    @Test
    void givenDraftOrder_whenPlace_thenTryToAddItem_shouldThrowOrderCannotBeEditedException() {
        Order order = Order.draft(new CustomerId());
        order.changeShipping(OrderTestDataBuilder.aShipping());
        order.changeBilling(OrderTestDataBuilder.aBilling());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        order.place();

        Product newProduct = ProductTestDataBuilder.aProductAltMousePad().build();
        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.addItem(newProduct, new Quantity(1)));
    }

    @Test
    void givenDraftOrder_whenPlace_thenTryToChangeShipping_shouldThrowOrderCannotBeEditedException() {
        Order order = Order.draft(new CustomerId());
        order.changeShipping(OrderTestDataBuilder.aShipping());
        order.changeBilling(OrderTestDataBuilder.aBilling());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        order.place();

        Shipping newShipping = OrderTestDataBuilder.aShipping();
        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeShipping(newShipping));
    }

    @Test
    void givenDraftOrder_whenPlace_thenTryToChangeBilling_shouldThrowOrderCannotBeEditedException() {
        Order order = Order.draft(new CustomerId());
        order.changeShipping(OrderTestDataBuilder.aShipping());
        order.changeBilling(OrderTestDataBuilder.aBilling());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        order.place();

        Billing newBilling = OrderTestDataBuilder.aBilling();
        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeBilling(newBilling));
    }

    @Test
    void givenDraftOrder_whenPlace_thenTryToChangePaymentMethod_shouldThrowOrderCannotBeEditedException() {
        Order order = Order.draft(new CustomerId());
        order.changeShipping(OrderTestDataBuilder.aShipping());
        order.changeBilling(OrderTestDataBuilder.aBilling());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        order.place();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changePaymentMethod(PaymentMethod.GATEWAY_BALANCE));
    }

    @Test
    void givenPlacedOrder_whenMarkAsPaid_thenTryToAddItem_shouldThrowOrderCannotBeEditedException() {
        Order order = Order.draft(new CustomerId());
        order.changeShipping(OrderTestDataBuilder.aShipping());
        order.changeBilling(OrderTestDataBuilder.aBilling());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        order.place();
        order.markAsPaid();

        Product newProduct = ProductTestDataBuilder.aProductAltMousePad().build();
        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.addItem(newProduct, new Quantity(1)));
    }

    @Test
    void givenPlacedOrder_whenMarkAsPaid_thenTryToChangeShipping_shouldThrowOrderCannotBeEditedException() {
        Order order = Order.draft(new CustomerId());
        order.changeShipping(OrderTestDataBuilder.aShipping());
        order.changeBilling(OrderTestDataBuilder.aBilling());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        order.place();
        order.markAsPaid();

        Shipping newShipping = OrderTestDataBuilder.aShipping();
        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeShipping(newShipping));
    }

}

