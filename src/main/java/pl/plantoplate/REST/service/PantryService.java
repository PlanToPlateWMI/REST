package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.WrongProductInShoppingList;
import pl.plantoplate.REST.repository.PantryRepository;

import java.util.List;

@Service
public class PantryService {

    private final PantryRepository pantryRepository;
    private final UserService userService;


    public PantryService(PantryRepository pantryRepository, UserService userService) {
        this.pantryRepository = pantryRepository;
        this.userService = userService;
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
                throw new WrongProductInShoppingList("User try to transfer to pantry not his product or product wasn't bought ");
        }

        for(long id:productId){
            ShopProduct shopProduct = pantryRepository.findById(id).get();
            shopProduct.setProductState(ProductState.PANTRY);
            pantryRepository.save(shopProduct);
        }

        return pantryRepository.findAllByProductStateAndGroup(ProductState.PANTRY, group);
    }

}
