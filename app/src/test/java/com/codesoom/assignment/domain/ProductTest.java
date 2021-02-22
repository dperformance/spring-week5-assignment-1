package com.codesoom.assignment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    @DisplayName("제품 id를 제외한 상품을 조회하여 값을 비교")
    void creationWithoutId() {
        Product product = Product.builder()
                .name("신발")
                .maker("나이키")
                .price(5000)
                .build();

        assertThat(product.getName()).isEqualTo("신발");
        assertThat(product.getMaker()).isEqualTo("나이키");
        assertThat(product.getPrice()).isEqualTo(5000);
        assertThat(product.getImageUrl()).isNull();
    }

}