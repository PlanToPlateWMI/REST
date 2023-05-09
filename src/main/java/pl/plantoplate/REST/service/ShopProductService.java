package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.shoppinglist.ShopProductGroup;
import pl.plantoplate.REST.repository.ShopProductGroupRepository;

@Service
public class ShopProductService {

    private final ShopProductGroupRepository shopProductGroupRepository;


    public ShopProductService(ShopProductGroupRepository shopProductGroupRepository) {
        this.shopProductGroupRepository = shopProductGroupRepository;
    }


    public void save(ShopProductGroup shopProductGroup){
        shopProductGroupRepository.save(shopProductGroup);
    }
}
