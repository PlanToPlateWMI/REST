package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void save(Product product){
        productRepository.save(product);
    }

    public Product findByName(String productName){
        return productRepository.findByName(productName).orElseThrow(RuntimeException::new);
    }
}
