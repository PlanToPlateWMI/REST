package pl.plantoplate.REST.service;

import pl.plantoplate.REST.controller.dto.model.IngredientQtUnit;

public class CalculateIngredientsService {


    static float calculateIngredientsQty(float proportionQty, IngredientQtUnit ingredientQtUnit){

        switch(ingredientQtUnit.getUnit()){
            // to nearest 0.5 (when less than 0.5 than return 0.5)
            case SZT: {
                if ((float)Math.round(proportionQty * ingredientQtUnit.getQty() * 2) / 2.0f < 0.5f)
                    return 0.5f;
                return (float)Math.round(proportionQty * ingredientQtUnit.getQty() * 2) / 2.0f;
            }
            // to nearest 0.05, if less than 0.005 - then return original value (round to 2 decimal)
            case L:
            case KG: {
                if(proportionQty * ingredientQtUnit.getQty() < 0.05)
                    return (float) (Math.round(proportionQty * ingredientQtUnit.getQty() * 100.0)/100.0);
                return (float) (Math.round(proportionQty * ingredientQtUnit.getQty() * 20) / 20.0);
            }
            // to nearest 5 (lower), if less than 5 - then return original value (rounded)
            case GR:
            case ML:{
                if(proportionQty * ingredientQtUnit.getQty() < 5)
                    return (float) Math.round(proportionQty * ingredientQtUnit.getQty());
                return (float) (5*(Math.floor(Math.abs(proportionQty * ingredientQtUnit.getQty()/5))));
            }
            default:
                return 1;
        }
    }
}
