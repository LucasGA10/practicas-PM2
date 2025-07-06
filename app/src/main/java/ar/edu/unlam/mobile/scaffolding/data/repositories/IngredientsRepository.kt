package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.Ingredient
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.IngredientType

object IngredientsRepository {

    fun getIngredients(): List<Ingredient> {
        return ingredients
    }

    private val ingredients = listOf(
        Ingredient(1, "Aceite de oliva", "https://example.com/aceite-oliva.jpg", IngredientType.Aceite),
        Ingredient(2, "Aceite vegetal", "https://example.com/aceite-vegetal.jpg", IngredientType.Aceite),
        Ingredient(3, "Agua", "https://example.com/agua.jpg", IngredientType.Líquido),
        Ingredient(4, "Ajo", "https://example.com/ajo.jpg", IngredientType.Condimento),
        Ingredient(5, "Ajo en polvo", "https://example.com/ajo-polvo.jpg", IngredientType.Condimento),
        Ingredient(6, "Ají molido", "https://example.com/aji-molido.jpg", IngredientType.Condimento),
        Ingredient(7, "Arvejas", "https://example.com/arvejas.jpg", IngredientType.Legumbre),
        Ingredient(8, "Bife de carne magra", "https://example.com/bife.jpg", IngredientType.Carne),
        Ingredient(9, "Brócoli", "https://example.com/brocoli.jpg", IngredientType.Verdura),
        Ingredient(10, "Carne magra", "https://example.com/carne-magra.jpg", IngredientType.Carne),
        Ingredient(11, "Carne picada magra", "https://example.com/carne-picada.jpg", IngredientType.Carne),
        Ingredient(12, "Cebolla", "https://example.com/cebolla.jpg", IngredientType.Verdura),
        Ingredient(13, "Cebolla morada", "https://example.com/cebolla-morada.jpg", IngredientType.Verdura),
        Ingredient(14, "Choclos frescos", "https://example.com/choclos.jpg", IngredientType.Cereal),
        Ingredient(15, "Coliflor", "https://example.com/coliflor.jpg", IngredientType.Verdura),
        Ingredient(16, "Espinaca fresca", "https://example.com/espinaca.jpg", IngredientType.Verdura),
        Ingredient(17, "Fideos integrales", "https://example.com/fideos.jpg", IngredientType.Cereal),
        Ingredient(18, "Huevos", "https://example.com/huevos.jpg", IngredientType.Huevo),
        Ingredient(19, "Jamón cocido", "https://example.com/jamon-cocido.jpg", IngredientType.Carne),
        Ingredient(20, "Jamón cocido natural bajo en sodio", "https://example.com/jamon-natural.jpg", IngredientType.Carne),
        Ingredient(21, "Jugo de limón", "https://example.com/limon.jpg", IngredientType.Líquido),
        Ingredient(22, "Laurel", "https://example.com/laurel.jpg", IngredientType.Condimento),
        Ingredient(23, "Lechuga", "https://example.com/lechuga.jpg", IngredientType.Verdura),
        Ingredient(24, "Leche", "https://example.com/leche.jpg", IngredientType.Lácteo),
        Ingredient(25, "Leche descremada", "https://example.com/leche-descremada.jpg", IngredientType.Lácteo),
        Ingredient(26, "Leche vegetal", "https://example.com/leche-vegetal.jpg", IngredientType.Líquido),
        Ingredient(27, "Lentejas", "https://example.com/lentejas.jpg", IngredientType.Legumbre),
        Ingredient(28, "Medallones de cerdo", "https://example.com/cerdo.jpg", IngredientType.Carne),
        Ingredient(29, "Mix de hojas verdes", "https://example.com/mix-verde.jpg", IngredientType.Verdura),
        Ingredient(30, "Mostaza", "https://example.com/mostaza.jpg", IngredientType.Condimento),
        Ingredient(31, "Morrón rojo", "https://example.com/morron.jpg", IngredientType.Verdura),
        Ingredient(32, "Orégano", "https://example.com/oregano.jpg", IngredientType.Condimento),
        Ingredient(33, "Palta", "https://example.com/palta.jpg", IngredientType.Fruta),
        Ingredient(34, "Pan integral", "https://example.com/pan-integral.jpg", IngredientType.Cereal),
        Ingredient(35, "Papas", "https://example.com/papas.jpg", IngredientType.Verdura),
        Ingredient(36, "Pechuga de pollo", "https://example.com/pechuga.jpg", IngredientType.Carne),
        Ingredient(37, "Pepino", "https://example.com/pepino.jpg", IngredientType.Verdura),
        Ingredient(38, "Perejil", "https://example.com/perejil.jpg", IngredientType.Condimento),
        Ingredient(39, "Pimiento", "https://example.com/pimiento.jpg", IngredientType.Verdura),
        Ingredient(40, "Pimiento rojo", "https://example.com/pimiento-rojo.jpg", IngredientType.Verdura),
        Ingredient(41, "Pimentón", "https://example.com/pimenton.jpg", IngredientType.Condimento),
        Ingredient(42, "Pimentón dulce", "https://example.com/pimenton-dulce.jpg", IngredientType.Condimento),
        Ingredient(43, "Puré de tomate", "https://example.com/pure-tomate.jpg", IngredientType.Verdura),
        Ingredient(44, "Queso bajo en grasa", "https://example.com/queso-bajo.jpg", IngredientType.Lácteo),
        Ingredient(45, "Queso magro rallado", "https://example.com/queso-magro.jpg", IngredientType.Lácteo),
        Ingredient(46, "Queso rallado", "https://example.com/queso-rallado.jpg", IngredientType.Lácteo),
        Ingredient(47, "Salsa de soja baja en sodio", "https://example.com/salsa-soja.jpg", IngredientType.Condimento),
        Ingredient(48, "Suprema de pollo", "https://example.com/suprema.jpg", IngredientType.Carne),
        Ingredient(49, "Tapa de cuadril", "https://example.com/cuadril.jpg", IngredientType.Carne),
        Ingredient(50, "Tomate", "https://example.com/tomate.jpg", IngredientType.Verdura),
        Ingredient(51, "Tomate cherry", "https://example.com/tomate-cherry.jpg", IngredientType.Verdura),
        Ingredient(52, "Tomate triturado", "https://example.com/tomate-triturado.jpg", IngredientType.Verdura),
        Ingredient(53, "Tomillo", "https://example.com/tomillo.jpg", IngredientType.Condimento),
        Ingredient(54, "Vinagre o jugo de limón", "https://example.com/vinagre.jpg", IngredientType.Líquido)
    )

}