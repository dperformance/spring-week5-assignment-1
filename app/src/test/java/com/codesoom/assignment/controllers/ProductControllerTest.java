// REST API (상품과 관련된 HTTP 요청 처리를 담당합니다.)
// 1. GET /products
// 2. GET /products/{id}
// 3. POST /products
// 4. PATCH /products/{id}
// 5. DELETE /products/{id}

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private ProductController productController;

    // @MockBean은 Spring ApplicationContext에 Mock(가짜)객체를 추가하게 해주는 어노태이션이다.
    @MockBean
    private ProductService productService;

    // 따라서 가짜 ProductService 객체에 given으로 작업을 하는것이다.
    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product build = Product.builder()
                                .name("신발")
                                .maker("아디다스")
                                .price(5000)
                                .imageUrl("adidas.png")
                                .build();
        products.add(build);

        given(productService.getProducts()).willReturn(products);

    }



    @Test
    void list() throws Exception{
        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("아디다스")));
    }


}