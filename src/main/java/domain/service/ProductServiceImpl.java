package domain.service;


import domain.dto.request.CreateProductRequest;
import domain.dto.request.DeleteProductRequest;
import domain.dto.request.UpdateProductRequest;
import domain.dto.response.ProductResponse;
import domain.entity.Product;
import domain.exception.ProductNotFoundException;
import domain.mapper.ProductMapper;
import domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Override
    public ProductResponse findById(Long productId) {
        Product product  =  productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
        return productMapper.toResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product saved = productRepository.save(product);
        log.info("Product created: id={}, name={}", saved.getProductId(), saved.getName());
        return productMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public void deleteProduct(DeleteProductRequest request) {

    }

    @Override
    public ProductResponse updateProduct(UpdateProductRequest request) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponse> getAllProducts() {
        return List.of();//немного хрень написала, просто не помню как найти все продукты
    }


}
