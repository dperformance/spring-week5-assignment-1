// REST API (상품과 관련된 HTTP 요청 처리를 담당합니다.)
// 1. GET /products -> list() ok
// 2. GET /products/{id} -> ok
// 3. POST /products ->
// 4. PATCH /products/{id}
// 5. DELETE /products/{id}

package com.codesoom.assignment.controllers;

import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.request.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long NOT_EXISTING_ID = 100L;

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
        Product product = Product.builder()
                                .name("신발")
                                .maker("아디다스")
                                .price(5000)
                                .imageUrl("adidas.png")
                                .build();
        products.add(product);

        given(productService.getProducts()).willReturn(List.of(product));

        given(productService.getProduct(EXISTING_ID)).willReturn(product);

        given(productService.getProduct(NOT_EXISTING_ID))
                .willThrow(new ProductNotFoundException(NOT_EXISTING_ID));

        given(productService.createProduct(any(ProductRequest.class))).willReturn(product);

        // willReturn에 product를 줘도 상관없다.
//        given(productService.updateProduct(eq(EXISTING_ID), any(Product.class)))
//                .willReturn(product);
        given(productService.updateProduct(eq(EXISTING_ID), any(ProductRequest.class)))
                .will(invocation -> {
                    Long id = invocation.getArgument(0);
                    ProductRequest source = invocation.getArgument(1);
////                    return Product.builder()
////                            .id(id)
////                            .name(source.getName())
////                            .maker(source.getMaker())
////                            .price(source.getPrice())
////                            .imageUrl(source.getImageUrl());
                    return new Product(
                                        id,
                                        source.getName(),
                                        source.getMaker(),
                                        source.getPrice(),
                                        source.getImageUrl());
                });

        given(productService.updateProduct(eq(NOT_EXISTING_ID), any(ProductRequest.class)))
                .willThrow(new ProductNotFoundException(NOT_EXISTING_ID));

        given(productService.deleteProduct(NOT_EXISTING_ID))
                .willThrow(new ProductNotFoundException(NOT_EXISTING_ID));
    }

    @Test
    @DisplayName("상품 목록을 조회하여 값을 확인합니다.")
    void list() throws Exception{
        mockMvc.perform(
                get("/products")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("아디다스")));
    }

    @Test
    @DisplayName("특정 상품을 조회한다.")
    void detailExistindId() throws Exception {
        // Actual : 404(Not Found) 찾을 수 없다.
        mockMvc.perform(
                get("/products/{id}", EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("신발")))
                .andExpect(content().string(containsString("아디다스")));
    }

    @Test
    @DisplayName("존재 하지 않는 상품을 조회")
    void detailNotExistindId() throws Exception {
        mockMvc.perform(get("/products/{id}", NOT_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상품 추가")
    void createWithValidAttributes() throws Exception {

        // 415 error 서버는 Json 형식으로 받기를 원하는데 client는 평문으로 보내서 생기는 error
        mockMvc.perform(
                post("/products")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"name\":\"신발\", \"maker\":\"아디다스\", \"price\":5000}")
                .content("{\"name\":\"신발\", \"maker\":\"아디다스\", \"price\":5000, \"imageUrl\":\"kodolth.png\"}")
        )
                .andExpect(status().isCreated())
//                .andExpect(content().string(containsString("{\"name\":\"신발\", \"maker\":\"아디다스\", \"price\":5000}"))); // @ResponseStatus(HttpStatus.CREATE) 선언
                .andExpect(content().string(containsString("신발"))); // @ResponseStatus(HttpStatus.CREATE) 선언
        
        verify(productService).createProduct(any(ProductRequest.class)); //가짜 객체인 Product를 매개변수로 넣어준다.
    }

    @Test
    @DisplayName("유효하지 않는 상품 추가시 400 Error 반환.") // BAD_REQUEST == 400
    void createWithInValidAttributes() throws Exception {
        // 415 error 서버는 Json 형식으로 받기를 원하는데 client는 평문으로 보내서 생기는 error
        // validation 미적용시 null이든 정상적인 데이터가 아니든 걸러낼 방법이 없기 때문에 201 정상 응답을 받는다.
        // Product Entity에 @NotBlank 어노테이션을 붙여도 201 정상 응답을 받는다.
        // ProductController에 create() @RequestBody @Valid 를 붙여줘야 한다.
        mockMvc.perform(
                post("/products")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\", \"maker\":\"\", \"price\":0}")
        )
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("상품을 찾아 값을 변경한다. ")
    void updateExistingId() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"신발\", \"maker\":\"아디다스\", \"price\":5000}")
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("아디다스")));
        verify(productService).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정요청시 예외를 던진다.") // NOT_FOUND == 404
    void updateNotExistingId() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", NOT_EXISTING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"코돌쓰로 바꿔죠\", \"maker\":\"돌쓰\", \"price\":500000}") // 변경하고자 하는 body데이터가 없으면 400 error을 발생
        )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유효하지 않는 값으로 수정요청시 400 Error 반환.") // BAD_REQUEST == 400
    void updateNotInvalidAttributes() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", EXISTING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\",\"}") // 변경하고자 하는 body데이터가 없으면 400 error을 발생
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품을 찾아 삭제하고 상태코드 204를 반환해준다.") // NO_CONTENT == 204
    void deleteExistingId() throws Exception {
        mockMvc.perform(delete("/products/{id}",EXISTING_ID))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(EXISTING_ID);
    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제요청시 예외를 던진다.") // NOT_FOUND == 404
    void deleteNotExistingId() throws Exception {
        mockMvc.perform(delete("/products/{id}", NOT_EXISTING_ID))
                .andExpect(status().isNotFound());
        verify(productService).deleteProduct(NOT_EXISTING_ID);
    }
}
