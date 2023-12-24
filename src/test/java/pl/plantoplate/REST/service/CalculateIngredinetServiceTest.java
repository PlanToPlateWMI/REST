package pl.plantoplate.REST.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.plantoplate.REST.controller.dto.model.IngredientQtUnit;
import pl.plantoplate.REST.entity.shoppinglist.Unit;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateIngredinetServiceTest {

    @ParameterizedTest
    @MethodSource("methodSource_L_KG")
    void calculate_L_KG(float proportionQty, IngredientQtUnit ingredientQtUnit, float expected){

        assertEquals(CalculateIngredientsService.calculateIngredientsQty(proportionQty, ingredientQtUnit), expected);
    }

     private static Stream<Arguments> methodSource_L_KG(){

        return Stream.of(
                Arguments.of(1, new IngredientQtUnit(0.04f, Unit.KG), 0.04f),
                Arguments.of(1, new IngredientQtUnit(0.04f, Unit.L), 0.04f),

                Arguments.of(1, new IngredientQtUnit(0.12f, Unit.KG), 0.1f),
                Arguments.of(1, new IngredientQtUnit(0.12f, Unit.L), 0.1f),

                Arguments.of(1, new IngredientQtUnit(0.178f, Unit.KG), 0.2f),
                Arguments.of(1, new IngredientQtUnit(0.178f, Unit.L), 0.2f),

                Arguments.of(1, new IngredientQtUnit(1.23f, Unit.KG), 1.25f),
                Arguments.of(1, new IngredientQtUnit(1.23f, Unit.L), 1.25f)
        );
    }

    @ParameterizedTest
    @MethodSource("methodSource_SZT")
    void calculate_SZT(float proportionQty, IngredientQtUnit ingredientQtUnit, float expected){

        assertEquals(CalculateIngredientsService.calculateIngredientsQty(proportionQty, ingredientQtUnit), expected);
    }

    private static Stream<Arguments> methodSource_SZT(){

        return Stream.of(
                Arguments.of(1, new IngredientQtUnit(0.3f, Unit.SZT), 0.5f),
                Arguments.of(1, new IngredientQtUnit(0.78f, Unit.SZT), 1f),
                Arguments.of(1, new IngredientQtUnit(1.38f, Unit.SZT), 1.5f)
        );
    }

    @ParameterizedTest
    @MethodSource("methodSource_GR_ML")
    void calculate_GR_ML(float proportionQty, IngredientQtUnit ingredientQtUnit, float expected){

        assertEquals(CalculateIngredientsService.calculateIngredientsQty(proportionQty, ingredientQtUnit), expected);
    }

    private static Stream<Arguments> methodSource_GR_ML(){

        return Stream.of(
                Arguments.of(1, new IngredientQtUnit(4f, Unit.GR), 4f),
                Arguments.of(1, new IngredientQtUnit(4f, Unit.ML), 4f),

                Arguments.of(1, new IngredientQtUnit(6f, Unit.GR), 5f),
                Arguments.of(1, new IngredientQtUnit(6f, Unit.ML), 5f),

                Arguments.of(1, new IngredientQtUnit(38f, Unit.GR), 35f),
                Arguments.of(1, new IngredientQtUnit(38f, Unit.ML), 35f),

                Arguments.of(1, new IngredientQtUnit(42f, Unit.GR), 40f),
                Arguments.of(1, new IngredientQtUnit(42f, Unit.ML), 40f)
        );
    }
}
