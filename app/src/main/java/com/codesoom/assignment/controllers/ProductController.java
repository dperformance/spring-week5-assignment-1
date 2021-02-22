// REST API (상품과 관련된 HTTP 요청 처리를 담당합니다.)
// 1. GET /products
// 2. GET /products/{id}
// 3. POST /products
// 4. PATCH /products/{id}
// 5. DELETE /products/{id}

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 상품과 관련된 HTTP 요청 처리를 담당합니다.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService; // 생성자를 만들어준다.

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public List<Product> list() {
        // 1. 컨트롤러에서 직접 데이터를 만들어 return 해주는 test를 진행한다.
//        Product product = Product.builder()
//                                .name("신발")
//                                .maker("아디다스")
//                                .price(5000)
//                                .imageUrl("adidas.png")
//                                .build();
//        return List.of(product);

        // 2. productService.getProducts()에서 값을 return해줄 수 있도록 처리한다.
        return productService.getProducts();


    }
}
