package domain.service;


import domain.dto.request.CreateProductRequest;
import domain.dto.request.PatchProductRequest;
import domain.dto.request.UpdateProductRequest;
import domain.dto.response.ProductResponse;
import domain.entity.Product;
import domain.entity.ReviewStats;
import domain.exception.ProductNotFoundException;
import domain.interfaces.ProductReviewStatsProjection;
import domain.mapper.ProductMapper;
import domain.repository.ProductRepository;
import domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ReviewRepository reviewRepository;


    @Cacheable(cacheNames = "products", key = "#p0")
    @Transactional(readOnly = true)
    @Override
    public ProductResponse findById(Long productId) {
        Product product  =  productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        ReviewStats stats = new ReviewStats(reviewRepository.countByProduct_ProductId(productId), reviewRepository.findAverageRatingByProductId(productId));
        return productMapper.toResponse(product, stats);
    }

    @Transactional
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product saved = productRepository.save(product);

        log.info("Product created: id={}, name={}", saved.getProductId(), saved.getName());

        return productMapper.toResponse(saved, ReviewStats.empty());
    }

    @Transactional(readOnly = true)
    @Override
    public void deleteProduct(Long productId) {
            if (!productRepository.existsById(productId)) {
                throw new ProductNotFoundException("Product not found with id: " + productId);
            }
            productRepository.deleteById(productId);
            log.info("Product deleted: id={}", productId);
        }

    @Transactional
    @Override
    public ProductResponse fullUpdateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));


        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCurrentPrice(request.getCurrentPrice());
        product.setDiscountPercent(request.getDiscountPercent());
        product.setQuantityAvailable(request.getQuantityAvailable());
        product.setCostPrice(request.getCostPrice());
        product.setSupplier(request.getSupplier());
        product.setImage(request.getImage());

        ReviewStats stats = new ReviewStats(reviewRepository.countByProduct_ProductId(productId), reviewRepository.findAverageRatingByProductId(productId));

        log.info("Product fully updated: id={}", productId);

        return productMapper.toResponse(product, stats);
    }

    @Transactional
    @Override
    public ProductResponse patchProduct(Long productId, PatchProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));


        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getCurrentPrice() != null) product.setCurrentPrice(request.getCurrentPrice());
        if (request.getDiscountPercent() != null) product.setDiscountPercent(request.getDiscountPercent());
        if (request.getQuantityAvailable() != null) product.setQuantityAvailable(request.getQuantityAvailable());
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getSupplier() != null) product.setSupplier(request.getSupplier());
        if (request.getImage() != null) product.setImage(request.getImage());

        ReviewStats stats = new ReviewStats(reviewRepository.countByProduct_ProductId(productId), reviewRepository.findAverageRatingByProductId(productId));

        log.info("Product patched: id={}", productId);

        return productMapper.toResponse(product, stats);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found: id=" + productId));
        ReviewStats stats = buildReviewStats(productId);

        return productMapper.toResponse(product, stats);
    }

    private ReviewStats buildReviewStats(Long productId) {
        long count = reviewRepository.countByProduct_ProductId(productId);
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        if (count == 0) {
            return new ReviewStats(0L, null);
        }

        return new ReviewStats(count, averageRating);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return List.of();

        List<Long> ids = products.stream()
                .map(Product::getProductId)
                .toList();

        Map<Long, ReviewStats> statsByProductId = reviewRepository.findStatsByProductIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        ProductReviewStatsProjection::getProductId,
                        row -> new ReviewStats(
                                row.getCount(),
                                row.getAverageRating()
                        )
                ));

        return products.stream()
                .map(p -> productMapper.toResponse(p, statsByProductId.getOrDefault
                        (p.getProductId(), ReviewStats.empty())))
                .toList();
    }
}
