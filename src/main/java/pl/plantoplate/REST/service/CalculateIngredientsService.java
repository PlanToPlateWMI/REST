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

import pl.plantoplate.REST.controller.dto.model.IngredientQtUnit;

/**
 * Service to calculate ingredients in planned meal depends on number of planned portions and
 * amount of products in original recipe
 */
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
