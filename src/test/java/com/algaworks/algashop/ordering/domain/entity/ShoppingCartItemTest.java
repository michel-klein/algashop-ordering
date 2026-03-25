package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.ShoppingCartItemIncompatibleProductException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartItemTest {

    @Test
    void shouldCalculateTotalAmountOnNewItemCreation() {
        ShoppingCartId cartId = new ShoppingCartId();
        ProductId productId = new ProductId();
        ProductName name = new ProductName("Test Product");
        Money price = new Money("100");
        Quantity quantity = new Quantity(3);
        Boolean available = true;

        ShoppingCartItem item = ShoppingCartItem.brandNew()
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(name)
                .price(price)
                .quantity(quantity)
                .available(available)
                .build();

        Assertions.assertThat(item.totalAmount()).isEqualTo(price.multiply(quantity));
    }

    @Test
    void shouldThrowExceptionWhenChangingQuantityToZero() {
        ShoppingCartId cartId = new ShoppingCartId();
        ProductId productId = new ProductId();
        ProductName name = new ProductName("Test Product");
        Money price = new Money("100");
        Quantity quantity = new Quantity(1);
        Boolean available = true;

        ShoppingCartItem item = ShoppingCartItem.brandNew()
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(name)
                .price(price)
                .quantity(quantity)
                .available(available)
                .build();

        Assertions.assertThatThrownBy(() -> item.changeQuantity(Quantity.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenRefreshingWithIncompatibleProduct() {
        ShoppingCartId cartId = new ShoppingCartId();
        ProductId productId = new ProductId();
        ProductName name = new ProductName("Test Product");
        Money price = new Money("100");
        Quantity quantity = new Quantity(1);
        Boolean available = true;

        ShoppingCartItem item = ShoppingCartItem.brandNew()
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(name)
                .price(price)
                .quantity(quantity)
                .available(available)
                .build();

        Product incompatibleProduct = ProductTestDataBuilder.aProductAltRamMemory().build();

        Assertions.assertThatThrownBy(() -> item.refresh(incompatibleProduct))
                .isInstanceOf(ShoppingCartItemIncompatibleProductException.class);
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        ShoppingCartItemId id = new ShoppingCartItemId();
        ShoppingCartId cartId = new ShoppingCartId();
        ProductId productId = new ProductId();
        ProductName name = new ProductName("Test Product");
        Money price = new Money("100");
        Quantity quantity = new Quantity(1);
        Boolean available = true;
        Money totalAmount = price.multiply(quantity);

        ShoppingCartItem item1 = ShoppingCartItem.existing()
                .id(id)
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(name)
                .price(price)
                .quantity(quantity)
                .available(available)
                .totalAmount(totalAmount)
                .build();

        ShoppingCartItem item2 = ShoppingCartItem.existing()
                .id(id)
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(name)
                .price(price)
                .quantity(quantity)
                .available(available)
                .totalAmount(totalAmount)
                .build();

        Assertions.assertThat(item1).isEqualTo(item2);
    }
}
