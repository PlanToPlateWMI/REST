/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.
 */

package pl.plantoplate.REST.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.UserRepository;

@Service
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final CategoryService categoryService;

    private final ProductService productService;

    private final ShopProductService shopProductService;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository, CategoryService categoryService, ProductService productService, ShopProductService shopProductService) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;

        this.categoryService = categoryService;
        this.productService = productService;
        this.shopProductService = shopProductService;
    }

    /**
     * Create new group and add user as admin to this group. Add custom product - "miód wielokwiatowy".
     * Dodanie do ShopProductGroup 2 produkty.
     * @param email
     */
    public void createGroupAndAddAdmin(String email) throws EntityNotFound {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ " + email + "] wasn't found"));
        user.setActive(true);

        if(user.getUserGroup() == null) {
            user.setRole(Role.ROLE_ADMIN);

            Group group = new Group();
            group.addUser(user);

            groupRepository.save(group);

            // create custom product for this new group
            String categoryName = "Inne";
            Category category = null;
            try {
                category = categoryService.findByName(categoryName);
            } catch (EntityNotFound categoryNotFound) {
                categoryNotFound.printStackTrace();
            }

            Product customProduct = new Product();
            customProduct.setName("Miód wielokwiatowy");
            customProduct.setCategory(category);
            customProduct.setUnit(Unit.GR);
            customProduct.setCreated_by(group);

            productService.save(customProduct);

            // add 2 products to shopping list of this group
            String productName1 = "Mleko";
            String productName2 = "Boczek";
            Product product1 = productService.findByName(productName1);
            Product product2 = productService.findByName(productName2);

            ShopProduct shopProduct = new ShopProduct();
            shopProduct.setProduct(product1);
            shopProduct.setGroup(group);
            shopProduct.setAmount(2);
            shopProduct.setBought(false);

            shopProductService.save(shopProduct);

            ShopProduct shopProduct2 = new ShopProduct();
            shopProduct2.setProduct(product2);
            shopProduct2.setGroup(group);
            shopProduct2.setAmount(3);
            shopProduct2.setBought(false);

            shopProductService.save(shopProduct2);

            log.info("User with email [" + email + "] created new group");
        }


    }


    @Transactional(readOnly = true)
    public Group findById(long id) throws EntityNotFound {
        return groupRepository.findById(id).orElseThrow(() -> new EntityNotFound("Group with id [" +id + "] not found"));
    }
}
