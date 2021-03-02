package com.codesoom.assignment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    @DisplayName("제품 id를 제외한 상품을 찾아 값을 비교합니다.")
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

    @Test
    @DisplayName("제품 id에 해당하는 상품을 찾아 값을 확인합니다.")
    void creationValidId() {
        Product product = Product.builder()
                                .name("신발")
                                .maker("나이키")
                                .price(10000)
                                .build();

//        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("신발");
        assertThat(product.getImageUrl()).isNull();
    }

    @Test
    void changeWith() {
        Product product = Product.builder()
                                    .name("인형")
                                    .maker("코돌쓰")
                                    .price(50000)
                                    .build();

        product.changeWith(Product.builder()
                                    .name("코돌인형")
                                    .maker("코끼리")
                                    .price(50)
                                    .build());

        assertThat(product.getName()).isEqualTo("코돌인형");
        assertThat(product.getMaker()).isEqualTo("코끼리");
        assertThat(product.getPrice()).isEqualTo(50);
    }
}