package pl.plantoplate.REST.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.NoValidProductWithAmount;
import pl.plantoplate.REST.repository.PantryRepository;

import java.util.List;

@Service
@Slf4j
public class PantryService {

    private final PantryRepository pantryRepository;
    private final UserService userService;
    private final ProductService productService;


    public PantryService(PantryRepository pantryRepository, UserService userService, ProductService productService) {
        this.pantryRepository = pantryRepository;
        this.userService = userService;
        this.productService = productService;
    }

    public List<ShopProduct> findProductsFromPantry(String email) {

        Group group = userService.findGroupOfUser(email);
        return pantryRepository.findAllByProductStateAndGroup(ProductState.PANTRY, group);
    }

    /**
     * Transfer products from shopping list to pantry (change product state to PATRY). If at least one product not found, user try to transfer not his product or
     * product doesn't have state BOUGHT - throws exception.
     * @param email - email of user
     * @param productId - idis of products
     * @return
     */
    public List<ShopProduct> transferProductToPantry(String email, long[] productId ) {
        Group group = userService.findGroupOfUser(email);

        for(long id:productId){
            ShopProduct shopProduct = pantryRepository.findById(id).orElseThrow(() -> new EntityNotFound("Shop product not found"));

            if(shopProduct.getProductState()!= ProductState.BOUGHT || !shopProduct.getGroup().equals(group))
                throw new NoValidProductWithAmount("User try to transfer to pantry not his product or product wasn't bought ");
        }

        for(long id:productId){
            ShopProduct shopProduct = pantryRepository.findById(id).get();
            shopProduct.setProductState(ProductState.PANTRY);
            pantryRepository.save(shopProduct);
        }

        return pantryRepository.findAllByProductStateAndGroup(ProductState.PANTRY, group);
    }

    public List<ShopProduct> addProductToPantry(long productId, int amount, String email) {

        Group group = userService.findGroupOfUser(email);

        if(amount <= 0 ){
            throw new NoValidProductWithAmount("Product amount cannot be negative or 0");
        }

        Product product = productService.findById(productId);

        List<Product> productsOfGroup = productService.generalAndProductsOfGroup(group.getId());

        if(productsOfGroup.stream().noneMatch(p -> p.getId() == productId)){
            log.info("User try to add product not from his list to shopping list");
            throw new NoValidProductWithAmount("User try to add product to shopping list not from his list");
        }

        // check if product with the same name nad unit already exists in pantry and
        // if it is so - sum amounts
        List<ShopProduct> toBuyProductOfGroupList = this.findProductsFromPantry(email);

        if(toBuyProductOfGroupList.stream().anyMatch(p -> p.getProduct().getName().equals(product.getName()) &&
                p.getProduct().getUnit().equals(product.getUnit()))){
            ShopProduct pantryProduct = pantryRepository.findByProductAndGroup(product, group).get();
            pantryProduct.setAmount(pantryProduct.getAmount() + amount);

            pantryRepository.save(pantryProduct);
            log.info("Product with id [" + productId + "] exists in shopping list. Modified his amount.");
        }else{
            ShopProduct pantryProduct = new ShopProduct(product, group, amount, ProductState.PANTRY);
            pantryRepository.save(pantryProduct);
            log.info("Product with id [" + productId + "] added to shopping list.");
        }


        return this.findProductsFromPantry(email);
    }



}
