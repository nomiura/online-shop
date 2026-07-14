package domain.controller;


import domain.entity.Product;
import domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
//    private final ProductService productService;
//
//
//    @GetMapping("/{id}")
//    public Product getProduct(@PathVariable Long productId) {
//        return productService.findBy(productId);
//
//    }

}
