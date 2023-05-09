package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.repository.ProductRepository;

import java.util.List;

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

    @Transactional(readOnly = true)
    public List<Product> getProductsOfGroup(long groupId) {
        return productRepository.findProductsByGroup(groupId);
    }





}
