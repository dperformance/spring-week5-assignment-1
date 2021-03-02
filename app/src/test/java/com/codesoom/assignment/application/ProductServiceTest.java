// Todo list
// 1. getProducts -> 목록
// 2. getProduct -> 상세 정보
// 3. createProduct -> 상품 추가
// 4. updateProduct -> 상품 수정
// 5. deleteProduct -> 상품 삭제
package com.codesoom.assignment.application;

import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.domain.ProductRepository;
import com.codesoom.assignment.dto.request.ProductRequest;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ProductServiceTest {

    private ProductService productService;
    private final ProductRepository productRepository = mock(ProductRepository.class);

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
//        productRepository = mock(ProductRepository.class);

        productService = new ProductService(mapper, productRepository);

        List<Product> products = new ArrayList<Product>();
        Product product = Product.builder()
                                    .name("신발")
                                    .maker("아디다스")
                                    .price(5000)
                                    .imageUrl("adidas.png")
                                    .build();
        products.add(product);

        given(productRepository.findAll()).willReturn(List.of(product));

        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        given(productRepository.save(any(Product.class))).will(invocation -> {
            Product source = invocation.getArgument(0);
            return Product.builder()
                            .id(2L)
                            .name(source.getName())
                            .maker(source.getMaker())
                            .price(source.getPrice())
                            .imageUrl(source.getImageUrl() )
                            .build();
        });

//        given(productRepository.findById(100L)).willReturn(Optional.empty());
//        given(productRepository.findById(100L))
//                .willThrow(new ProductNotFoundException(100L));


    }

    @Test
    @DisplayName("상품 목록이 존재 하지 않을때 비어있는 값을 반환")
    void getProductsWithNoProduct() throws Exception {
//        assertThat(productService.getProducts()).isNull(); // return null;
        given(productRepository.findAll()).willReturn(List.of()); // 비어있는 List값을 반환
        assertThat(productService.getProducts()).isEmpty();
    }

    @Test
    @DisplayName("상품 목록을 조회하고 값을 확인한다.")
    void getProductsWithProduct() {
        // assertThat(productService.getProducts()).isEmpty(); // setUp에 설정 된 product List값을 반환
        // assertThat(productService.getProducts()).isNotNull();
        // assertThat(productService.getProducts()).hasSize(1); // Products의 size는 1개

        List<Product> products = productService.getProducts();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);

        Product product = products.get(0);
        assertThat(product.getName()).isEqualTo("신발");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("특정 상품 목록을 조회하고 값을 확인한다.")
    void getProductExistingId() {
//        assertThat(productService.getProduct(1L)).isNull();
        Product product = productService.getProduct(1L);
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("신발");

        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("미 존재 상품 조회 요청시 예외를 던진다.")
    void getProductNotExistingId() {
        assertThatThrownBy(() -> productService.getProduct(100L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("입력한 상품을 저장하고, 저장된 상품값을 리턴한다.")
    void createProduct() {
//        Product source = Product.builder()
//                .name("인형")
//                .maker("코돌쓰")
//                .price(5000)
//                .imageUrl("kodolth.png")
//                .build();

        // 아래 given 은 create 2L을 만들어주기 위해 사용한다.
//        given(productRepository.save(any(Product.class))).will(invocation -> {
//            Product source = invocation.getArgument(0);
//            return Product.builder()
//                            .id(2L)
//                            .name(source.getName())
//                            .maker(source.getMaker())
//                            .price(source.getPrice())
//                            .imageUrl(source.getImageUrl() )
//                            .build();
//        });

        Product product = productService.createProduct(ProductRequest.builder()
                                                                .name("인형")
                                                                .maker("코돌쓰")
                                                                .price(5000)
                                                                .imageUrl("kodolth.png")
                                                                .build());

//        verify(productRepository).save(product); product를 넘겨주면 다르다는 error가 발생한다 찾아보자
        verify(productRepository).save(any(Product.class));

        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getName()).isEqualTo("인형");
        assertThat(product.getMaker()).isEqualTo("코돌쓰");
        assertThat(product.getPrice()).isEqualTo(5000);
    }

    @Test
    @DisplayName("상품 목록을 수정한 뒤 목록 반환")
    void updateProductWithExistingProduct() {
        ProductRequest request = ProductRequest.builder()
                                .name("동물인형")
                                .maker("코끼리")
                                .price(50000)
                                .imageUrl("adidas.png")
                                .build();

        Product product = productService.updateProduct(1L, request);

        verify(productRepository).findById(1L);
        assertThat(product.getName()).isEqualTo("동물인형");
        assertThat(product.getMaker()).isEqualTo("코끼리");
        assertThat(product.getPrice()).isEqualTo(50000);
        assertThat(product.getImageUrl()).isEqualTo("adidas.png");
    }

    @Test
    @DisplayName("미 존재 상품 수정 요청시 NOT_FOUND(404)")
    void updateProductWithNotExistingProduct() {
//        Product source = Product.builder()
//                                .name("동물인형")
//                                .maker("코끼리")
//                                .price(50000)
//                                .imageUrl("adidas.png")
//                                .build();

//        assertThatThrownBy(() -> productService.updateProduct(1000L, source))
//                .isInstanceOf(ProductNotFoundException.class);

        // 위 처럼 source를 만들어서 진행해도 되지만 any를 이용하여 코드를 간결화시킨다.
        assertThatThrownBy(() -> productService.updateProduct(1000L, any(ProductRequest.class)))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("delte입니다.")
    void deleteProductWithExistingId() {
        productService.deleteProduct(1L);

        verify(productRepository).delete(any(Product.class));
    }

    @Test
    @DisplayName("미 존재 상품삭제 요청시 Not Found(404) 예외 던짐")
    void deleteProductWithNotExistingId() {
        assertThatThrownBy(() -> productService.deleteProduct(1000L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
