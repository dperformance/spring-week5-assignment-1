package com.codesoom.assignment.dto.request;

import com.codesoom.assignment.domain.Product;
import com.github.dozermapper.core.Mapping;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductRequest {
    private Long id;

    @NotBlank
    @Mapping("name")
    private String name;

    @NotBlank
    @Mapping("maker")
    private String maker;

    @NotNull
    @Mapping("price")
    private Integer price;

    @Mapping("imageUrl")
    private String imageUrl;

    public Product toEntity() {
        return Product.builder()
                .name(this.name)
                .maker(this.maker)
                .price(this.price)
                .imageUrl(this.imageUrl)
                .build();
    }
}
