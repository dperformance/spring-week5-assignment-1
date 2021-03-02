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
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final Mapper mapper;
    private final ProductRepository productRepository;

    public ProductService(Mapper dozerMapper,
                          ProductRepository productRepository
    ) {
        this.mapper = dozerMapper;
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return findProduct(id);
    }

    public Product createProduct(ProductRequest request) {
//        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
//        DestinationObject destObject = mapper.map(sourceObject, DestinationObject.class);

        // builder 등을 사용하여 product를 넘겨주는데 DozerMapper를 사용하여 간결하게 만든다.
        Product product = mapper.map(request, Product.class);
        return productRepository.save(product);
//        return productRepository.save(request.toEntity());
    }

    public Product updateProduct(Long id, ProductRequest request) {

        Product product = findProduct(id);

//        Product updateProduct = product.update(request.toEntity());
        Product updateProduct = product.changeWith(mapper.map(request, Product.class));

        return productRepository.save(updateProduct);
    }

    public Product deleteProduct(Long id) {
        Product product = findProduct(id);

        productRepository.delete(product);

        return product;
    }

    /**
     * getProduct() / updateProduct()에서 중복으로 발생하는 로직
     *
     * @param id
     * @return
     */
    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
