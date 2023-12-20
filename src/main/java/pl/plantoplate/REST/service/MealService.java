package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.controller.dto.model.IngredientQtUnit;
import pl.plantoplate.REST.controller.dto.model.MealProductQty;
import pl.plantoplate.REST.controller.dto.request.AddRecipeToShoppingList;
import pl.plantoplate.REST.controller.dto.request.PlanMealBasedOnRecipeRequestV1;
import pl.plantoplate.REST.controller.dto.request.PlanMealBasedOnRecipeRequestV2;
import pl.plantoplate.REST.controller.dto.response.MealOverviewResponse;
import pl.plantoplate.REST.controller.utils.MealType;
import pl.plantoplate.REST.entity.Synchronization;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.meal.MealIngredient;
import pl.plantoplate.REST.entity.meal.MealIngredientId;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.NotValidGroup;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.firebase.PushNotificationService;
import pl.plantoplate.REST.repository.MealIngredientRepository;
import pl.plantoplate.REST.repository.MealsRepository;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealsRepository mealsRepository;
    private final RecipeService recipeService;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ProductService productService;
    private final MealIngredientRepository mealIngredientRepository;
    private final PushNotificationService pushNotificationService;
    private final UserService userService;
    private final ShoppingListService shoppingListService;
    private final SynchronizationService synchronizationService;

    public MealService(MealsRepository mealsRepository, RecipeService recipeService, RecipeIngredientRepository recipeIngredientRepository, ProductService productService, MealIngredientRepository mealIngredientRepository, PushNotificationService pushNotificationService, UserService userService, ShoppingListService shoppingListService, SynchronizationService synchronizationService) {
        this.mealsRepository = mealsRepository;
        this.recipeService = recipeService;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.productService = productService;
        this.mealIngredientRepository = mealIngredientRepository;
        this.pushNotificationService = pushNotificationService;
        this.userService = userService;
        this.shoppingListService = shoppingListService;
        this.synchronizationService = synchronizationService;
    }

    public void planMealV1(PlanMealBasedOnRecipeRequestV1 planMeal, Group group, String email) {

        long recipeId = planMeal.getRecipeId();
        AddRecipeToShoppingList addRecipeToShoppingList = new AddRecipeToShoppingList();
        addRecipeToShoppingList.setRecipeId(recipeId);
        addRecipeToShoppingList.setPortions(planMeal.getPortions());
        addRecipeToShoppingList.setIngredientsId(planMeal.getIngredients());
        shoppingListService.addProductsToShoppingList(addRecipeToShoppingList, email);

        if (planMeal.getDate() == null)
            return;

        Recipe recipe = recipeService.findById(recipeId);
        if (planMeal.getDate().isBefore(LocalDate.now()))
            throw new WrongRequestData("Wrong date");
        try {
            MealType.valueOf(planMeal.getMealType());
        } catch (IllegalArgumentException e) {
            throw new WrongRequestData("Meal types available - " + Arrays.stream(MealType.values()).map(Enum::name).collect(Collectors.toList()));
        }

        Meal meal = new Meal();
        meal.setMealType(planMeal.getMealType());
        meal.setPortions(planMeal.getPortions());
        meal.setDate(planMeal.getDate());
        meal.setRecipe(recipe);
        meal.setGroup(group);
        mealsRepository.save(meal);
        long mealId = meal.getId();

        Map<Long, IngredientQtUnit> ingredientIdToUnitQtyInOriginalRecipe = recipeIngredientRepository.findAllByRecipe(recipe).stream().collect(Collectors.toMap(r -> r.getIngredient().getId(), r -> new IngredientQtUnit(r.getQty(), r.getIngredient().getUnit())));
        List<Long> ingredientIdsList = planMeal.getIngredients();
        long portionsInOriginalRecipe = recipe.getPortions();
        long portionsPlanned = planMeal.getPortions();
        float proportionIngredientQty = (float)portionsPlanned / (float)portionsInOriginalRecipe;
        for (Long ingredientToPlanId : ingredientIdsList) {
            IngredientQtUnit originalQtyUnit = ingredientIdToUnitQtyInOriginalRecipe.get(ingredientToPlanId);
            MealIngredient mealIngredient = new MealIngredient();
            mealIngredient.setMeal(meal);
            mealIngredient.setIngredient(this.productService.findById(ingredientToPlanId));
            float calculatedQty = CalculateIngredientsService.calculateIngredientsQty(proportionIngredientQty, originalQtyUnit);
            mealIngredient.setQty(calculatedQty);
            mealIngredient.setMealIngredientId(new MealIngredientId(mealId, ingredientToPlanId));
            mealIngredientRepository.save(mealIngredient);
            synchronizationService.saveSynchronizationIngredient(calculatedQty, group, ingredientToPlanId);
        }
        List<String> tokens = this.userService.getUserOfTheSameGroup(email).stream().map(User::getFcmToken).collect(Collectors.toList());
        this.pushNotificationService.sendAll(tokens, "Posiłek " + recipe.getTitle() + " został zaplanowany na " + convertMealType(MealType.valueOf(planMeal.getMealType())) + " " + planMeal.getDate().toString());
    }

    private String convertMealType(MealType mealType){
        Map<MealType, String> convert = new HashMap<>();
        convert.put(MealType.BREAKFAST, "śniadanie");
        convert.put(MealType.DINNER, "kolację");
        convert.put(MealType.LUNCH, "obiad");
        return convert.get(mealType);
    }

    public void planMealV2(PlanMealBasedOnRecipeRequestV2 planMeal, Group group, String email) {

        long recipeId = planMeal.getRecipeId();
        Recipe recipe = this.recipeService.findById(recipeId);
        if (planMeal.getDate().isBefore(LocalDate.now()))
            throw new WrongRequestData("Wrong date");
        try {
            MealType.valueOf(planMeal.getMealType());
        } catch (IllegalArgumentException e) {
            throw new WrongRequestData("Meal types available - " + Arrays.stream(MealType.values()).map(Enum::name).collect(Collectors.toList()));
        }
        Meal meal = new Meal();
        meal.setMealType(planMeal.getMealType());
        meal.setPortions(planMeal.getPortions());
        meal.setDate(planMeal.getDate());
        meal.setRecipe(recipe);
        meal.setGroup(group);

        this.mealsRepository.save(meal);
        long mealId = meal.getId();
        Map<Long, IngredientQtUnit> ingredientIdToUnitQtyInOriginalRecipe = this.recipeIngredientRepository.findAllByRecipe(recipe).stream().collect(Collectors.toMap(r -> Long.valueOf(r.getIngredient().getId()), r -> new IngredientQtUnit(r.getQty(), r.getIngredient().getUnit())));
        List<Long> ingredientIdsList = planMeal.getIngredients();
        long portionsInOriginalRecipe = recipe.getPortions();
        long portionsPlanned = planMeal.getPortions();
        float proportionIngredientQty = (float)portionsPlanned / (float)portionsInOriginalRecipe;

        for (long ingredientToPlanId : ingredientIdsList) {
            IngredientQtUnit originalQtyUnit = ingredientIdToUnitQtyInOriginalRecipe.get(ingredientToPlanId);
            MealIngredient mealIngredient = new MealIngredient();
            mealIngredient.setMeal(meal);
            mealIngredient.setIngredient(this.productService.findById(ingredientToPlanId));
            float calculatedQty = CalculateIngredientsService.calculateIngredientsQty(proportionIngredientQty, originalQtyUnit);
            mealIngredient.setQty(calculatedQty);
            mealIngredient.setMealIngredientId(new MealIngredientId(mealId, ingredientToPlanId));
            this.mealIngredientRepository.save(mealIngredient);
            if (planMeal.isProductsAdd() && planMeal.isSynchronize()) {
                float qtyOfPlannedMeal = calculatedQty;
                Optional<ShopProduct> pantryProduct = this.shoppingListService.getProducts(email, ProductState.PANTRY).stream().filter(p -> (p.getProduct().getId() == ingredientToPlanId)).findFirst();
                Optional<Synchronization> synchronization = this.synchronizationService.getByProductAndGroup(this.productService.findById(ingredientToPlanId), group);
                float qtyPantry = 0.0F;
                float qtySynchronization = 0.0F;
                if (pantryProduct.isPresent())
                    qtyPantry = pantryProduct.get().getAmount();
                if (synchronization.isPresent())
                    qtySynchronization = synchronization.get().getQty();
                if (qtyPantry < qtyOfPlannedMeal + qtySynchronization)
                    if (qtyOfPlannedMeal + qtySynchronization - qtyPantry > qtyOfPlannedMeal) {
                        this.shoppingListService.addProductToShoppingList(ingredientToPlanId, qtyOfPlannedMeal, email);
                    } else {
                        this.shoppingListService.addProductToShoppingList(ingredientToPlanId, qtyOfPlannedMeal + qtySynchronization - qtyPantry, email);
                    }
            }
            this.synchronizationService.saveSynchronizationIngredient(calculatedQty, group, ingredientToPlanId);
        }
        if (!planMeal.isSynchronize() && planMeal.isProductsAdd()) {
            AddRecipeToShoppingList addRecipeToShoppingList = new AddRecipeToShoppingList();
            addRecipeToShoppingList.setRecipeId(recipeId);
            addRecipeToShoppingList.setPortions(planMeal.getPortions());
            addRecipeToShoppingList.setIngredientsId(planMeal.getIngredients());
            this.shoppingListService.addProductsToShoppingList(addRecipeToShoppingList, email);
        }
        List<String> tokens = this.userService.getUserOfTheSameGroup(email).stream().map(User::getFcmToken).collect(Collectors.toList());
        this.pushNotificationService.sendAll(tokens, "Posiłek " + recipe.getTitle() + " został zaplanowany na " + convertMealType(MealType.valueOf(planMeal.getMealType())) + " " + planMeal.getDate().toString());
    }

    public List<MealOverviewResponse> getMealOverviewByDate(LocalDate localDate, Group userGroup) {
        List<Meal> mealsPlannedProvidedDate = userGroup.getPlannedMeals().stream().filter(m -> m.getDate().isEqual(localDate)).collect(Collectors.toList());
        return mealsPlannedProvidedDate.stream().map(m -> new MealOverviewResponse(m.getId(), m.getRecipe().getTitle(), m.getRecipe().getTime(), m.getMealType(), m.getRecipe().getImage_source(), m.getRecipe().isVege(), m.isPrepared()))
                .collect(Collectors.toList());
    }

    public MealProductQty findMealDetailById(long id, Group group) {
        Meal meal = findMealById(id);
        long mealGroupId = meal.getGroup().getId();
        if (mealGroupId != group.getId())
            throw new NotValidGroup("Meal with id [" + id + "] not found in lists of meals of user's group");
        Map<Product, Float> ingredientQuantity = new HashMap<>();
        List<MealIngredient> mealIngredients = this.mealIngredientRepository.findAllByMeal(meal);
        for (MealIngredient mealIngredient : mealIngredients)
            ingredientQuantity.put(mealIngredient.getIngredient(), mealIngredient.getQty());
        return new MealProductQty(meal, ingredientQuantity);
    }

    public Meal findMealById(long mealId) {
        return this.mealsRepository.findById(mealId).orElseThrow(() -> new EntityNotFound("Meal with id [" + mealId + "] was not found."));
    }

    @Transactional
    public void deleteMealById(long mealId, Group group) {
        Meal meal = findMealById(mealId);
        if (!meal.getGroup().getId().equals(group.getId()))
            throw new NotValidGroup("Meal with id [" + mealId + "] not found in lists of meals of user's group");
        for (MealIngredient mealIngredient : this.mealIngredientRepository.findAllByMeal(meal))
            this.synchronizationService.deleteSynchronizationIngredient(group, mealIngredient);
        this.mealIngredientRepository.deleteByMeal(meal);
        this.mealIngredientRepository.flush();
        this.mealsRepository.delete(meal);
    }

    public void prepareMeal(long mealId, Group group, String email) {
        Meal meal = findMealById(mealId);
        long mealGroupId = meal.getGroup().getId();
        if (mealGroupId != group.getId())
            throw new NotValidGroup("Meal with id [" + mealId + "] not found in lists of meals of user's group");
        if (meal.isPrepared())
            throw new NotValidGroup("Meal with id [" + mealId + "] have been already prepared");
        meal.setPrepared(true);
        this.mealsRepository.save(meal);
        List<MealIngredient> mealIngredients = mealIngredientRepository.findAllByMeal(meal);
        for (MealIngredient mealIngredient : mealIngredients)
            synchronizationService.deleteSynchronizationIngredient(group, mealIngredient);
        for (MealIngredient mealIngredient : mealIngredients) {
            Optional<ShopProduct> pantryProduct = shoppingListService.getProducts(email, ProductState.PANTRY).stream().filter(p -> (p.getProduct().getId() == mealIngredient.getIngredient().getId())).findFirst();
            float qtyPantry = 0.0F;
            float qtyMeal = mealIngredient.getQty();
            if (pantryProduct.isPresent()) {
                qtyPantry = pantryProduct.get().getAmount();
                ShopProduct shopProduct = pantryProduct.get();
                if (qtyPantry <= qtyMeal) {
                    shoppingListService.deleteProduct(shopProduct.getId(), email);
                    continue;
                }
                shopProduct.setAmount(qtyPantry - qtyMeal);
                shoppingListService.save(shopProduct);
            }
        }
    }

    public List<Meal> getMealsByBeforePlannedDate(LocalDate localDate){
        return mealsRepository.findAllByDateBefore(localDate);
    }

    public void deleteAll(List<Meal> meal){
        mealsRepository.deleteAll(meal);
    }

}