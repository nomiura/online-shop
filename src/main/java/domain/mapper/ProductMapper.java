package domain.mapper;

import domain.dto.request.CreateProductRequest;
import domain.dto.response.ProductResponse;
import domain.entity.Product;
import org.springframework.stereotype.Component;


@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        if(product == null) return null;
        return new ProductResponse(product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getCurrentPrice(),
                product.getDiscountPercent(),
                product.getEffectivePrice(),
                product.getImage(),
                product.isInStock()
        );
    }

    public Product toEntity(CreateProductRequest request) {
        if (request == null) return null;
          Product product = new Product();
          product.setName(request.getName());
          product.setDescription(request.getDescription());
          product.setCurrentPrice(request.getCurrentPrice());
          product.setDiscountPercent(request.getDiscountPercent());
          product.setImage(request.getImage());
          product.isInStock();
          product.setQuantityAvailable(request.getQuantityAvailable());
        return  product;
    }
}
