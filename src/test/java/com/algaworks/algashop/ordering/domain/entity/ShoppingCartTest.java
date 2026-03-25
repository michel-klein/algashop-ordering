package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainItemException;
import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainProductException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ShoppingCartTest {

    @Test
    void shouldHaveEmptyCartAfterCreation() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());

        Assertions.assertThat(cart.totalAmount()).isEqualTo(Money.ZERO);
        Assertions.assertThat(cart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenAddingOutOfStockProduct() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product outOfStockProduct = ProductTestDataBuilder.aProductUnavailable().build();

        Assertions.assertThatThrownBy(() -> cart.addItem(outOfStockProduct, new Quantity(1)))
                .isInstanceOf(ProductOutOfStockException.class);
    }

    @Test
    void shouldSumQuantityWhenAddingSameProductTwice() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct().build();
        Quantity initialQuantity = new Quantity(2);
        Quantity additionalQuantity = new Quantity(3);

        cart.addItem(product, initialQuantity);
        cart.addItem(product, additionalQuantity);

        ShoppingCartItem item = cart.findItem(product.id());
        Assertions.assertThat(item.quantity()).isEqualTo(new Quantity(5));
        Assertions.assertThat(cart.totalItems()).isEqualTo(new Quantity(5));
        Assertions.assertThat(cart.totalAmount()).isEqualTo(product.price().multiply(new Quantity(5)));
    }

    @Test
    void shouldAddTwoDifferentProducts() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product product1 = ProductTestDataBuilder.aProduct().build();
        Product product2 = ProductTestDataBuilder.aProductAltRamMemory().build();

        cart.addItem(product1, new Quantity(1));
        cart.addItem(product2, new Quantity(2));

        Assertions.assertThat(cart.items()).hasSize(2);
        Assertions.assertThat(cart.totalItems()).isEqualTo(new Quantity(3));
        Assertions.assertThat(cart.totalAmount()).isEqualTo(
                product1.price().multiply(new Quantity(1)).add(
                        product2.price().multiply(new Quantity(2))
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentItem() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());

        Assertions.assertThatThrownBy(() -> cart.removeItem(new ShoppingCartItemId()))
                .isInstanceOf(ShoppingCartDoesNotContainItemException.class);
    }

    @Test
    void shouldEmptyCart() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct().build();
        cart.addItem(product, new Quantity(1));

        cart.empty();

        Assertions.assertThat(cart.isEmpty()).isTrue();
        Assertions.assertThat(cart.totalAmount()).isEqualTo(Money.ZERO);
        Assertions.assertThat(cart.totalItems()).isEqualTo(Quantity.ZERO);
    }

    @Test
    void shouldThrowExceptionWhenRefreshingWithIncompatibleProduct() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct().build();
        cart.addItem(product, new Quantity(1));

        Product differentProduct = ProductTestDataBuilder.aProductAltRamMemory().build();

        Assertions.assertThatThrownBy(() -> cart.refreshItem(differentProduct))
                .isInstanceOf(ShoppingCartDoesNotContainProductException.class);
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        ShoppingCartId id = new ShoppingCartId();
        CustomerId customerId = new CustomerId();
        ShoppingCart cart1 = ShoppingCart.existing()
                .id(id)
                .customerId(customerId)
                .totalAmount(Money.ZERO)
                .totalItems(Quantity.ZERO)
                .createdAt(java.time.OffsetDateTime.now())
                .items(Set.of())
                .build();
        ShoppingCart cart2 = ShoppingCart.existing()
                .id(id)
                .customerId(customerId)
                .totalAmount(Money.ZERO)
                .totalItems(Quantity.ZERO)
                .createdAt(java.time.OffsetDateTime.now())
                .items(Set.of())
                .build();

        Assertions.assertThat(cart1).isEqualTo(cart2);
    }
}
