// 쇼핑몰 만들기
// Product 모델
// User 모델
// Order 모델
// ... 모델
// Application (UseCase)
// Product -> 관리자 등록/수정/삭제 -> list/detail
// 사용자 -> 주문 -> 확인 -> 배송 등 처리

// Product
// 0. 식별자 - identifier (ID)
// 1. 제품이름 - 신발
// 2. 제조사 - 아다스
// 3. 가격 - 5,000원 (판매가)
// 4. 이미지 - static, CDN => image URL

package com.codesoom.assignment.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Product {

    // Prpduct는 JPA의 Entity로 사용할 것이기 때문에 final을 붙여줄 경우 문제가 된다.
    private Long id;
    private String name;
    private String maker;
    private Integer price;
    private String imageUrl;

    @Builder
    public Product( String name, String maker, Integer price, String imageUrl) {
        this.name = name;
        this.maker = maker;
        this.price = price;
        this.imageUrl = imageUrl;
    }

}

