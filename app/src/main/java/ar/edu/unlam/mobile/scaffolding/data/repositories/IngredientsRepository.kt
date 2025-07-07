package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.Ingredient
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.IngredientType

object IngredientsRepository {

    fun getIngredients(): List<Ingredient> {
        return ingredients
    }

    private val ingredients = listOf(
        Ingredient("Aceite de oliva", "https://example.com/aceite-oliva.jpg", IngredientType.Aceite),
        Ingredient("Aceite vegetal", "https://example.com/aceite-vegetal.jpg", IngredientType.Aceite),
        Ingredient("Agua", "https://example.com/agua.jpg", IngredientType.Líquido),
        Ingredient("Ajo", "https://example.com/ajo.jpg", IngredientType.Verdura),
        Ingredient("Ajo en polvo", "https://example.com/ajo-polvo.jpg", IngredientType.Condimento),
        Ingredient("Ají molido", "https://example.com/aji-molido.jpg", IngredientType.Condimento),
        Ingredient("Arvejas", "https://example.com/arvejas.jpg", IngredientType.Legumbre),
        Ingredient("Avena en hojuelas", "https://example.com/avena.jpg", IngredientType.Cereal),
        Ingredient("Banana", "https://example.com/berenjena.jpg", IngredientType.Verdura),
        Ingredient("Bife de carne magra", "https://example.com/bife.jpg", IngredientType.Carne),
        Ingredient("Brócoli", "https://example.com/brocoli.jpg", IngredientType.Verdura),
        Ingredient("Cacao en polvo", "https://example.com/calabacin.jpg", IngredientType.Condimento),
        Ingredient("Carne magra", "https://example.com/carne-magra.jpg", IngredientType.Carne),
        Ingredient("Carne picada magra", "https://example.com/carne-picada.jpg", IngredientType.Carne),
        Ingredient("Cebolla", "https://example.com/cebolla.jpg", IngredientType.Verdura),
        Ingredient("Cebolla morada", "https://example.com/cebolla-morada.jpg", IngredientType.Verdura),
        Ingredient("Choclos frescos", "https://example.com/choclos.jpg", IngredientType.Cereal),
        Ingredient("Coliflor", "https://example.com/coliflor.jpg", IngredientType.Verdura),
        Ingredient("Garbanzos cocidos", "https://example.com/Garbanzos.jpg", IngredientType.Verdura),
        Ingredient("Espinaca fresca", "https://example.com/espinaca.jpg", IngredientType.Verdura),
        Ingredient("Fideos integrales", "https://example.com/fideos.jpg", IngredientType.Cereal),
        Ingredient("Frutillas", "https://example.com/garbanzos.jpg", IngredientType.Fruta),
        Ingredient("Huevos", "https://example.com/huevos.jpg", IngredientType.Huevo),
        Ingredient("Jamón cocido", "https://example.com/jamon-cocido.jpg", IngredientType.Carne),
        Ingredient("Jamón cocido natural bajo en sodio", "https://example.com/jamon-natural.jpg", IngredientType.Carne),
        Ingredient("Jugo de limón", "https://example.com/limon.jpg", IngredientType.Líquido),
        Ingredient("Laurel", "https://example.com/laurel.jpg", IngredientType.Condimento),
        Ingredient("Lechuga", "https://example.com/lechuga.jpg", IngredientType.Verdura),
        Ingredient("Leche", "https://example.com/leche.jpg", IngredientType.Lácteo),
        Ingredient("Leche descremada", "https://example.com/leche-descremada.jpg", IngredientType.Lácteo),
        Ingredient("Leche vegetal", "https://example.com/leche-vegetal.jpg", IngredientType.Líquido),
        Ingredient("Lentejas", "https://example.com/lentejas.jpg", IngredientType.Legumbre),
        Ingredient("Medallones de cerdo", "https://example.com/cerdo.jpg", IngredientType.Carne),
        Ingredient("Miel", "https://example.com/miel.jpg", IngredientType.Líquido),
        Ingredient("Miel o agave", "https://example.com/miel.jpg", IngredientType.Líquido),
        Ingredient("Mix de hojas verdes", "https://example.com/mix-verde.jpg", IngredientType.Verdura),
        Ingredient("Mostaza", "https://example.com/mostaza.jpg", IngredientType.Condimento),
        Ingredient("Morrón rojo", "https://example.com/morron.jpg", IngredientType.Verdura),
        Ingredient("Orégano", "https://example.com/oregano.jpg", IngredientType.Condimento),
        Ingredient("Palta", "https://example.com/palta.jpg", IngredientType.Fruta),
        Ingredient("Pan integral", "https://example.com/pan-integral.jpg", IngredientType.Cereal),
        Ingredient("Papas", "https://example.com/papas.jpg", IngredientType.Verdura),
        Ingredient("Pechuga de pollo", "https://example.com/pechuga.jpg", IngredientType.Carne),
        Ingredient("Pepino", "https://example.com/pepino.jpg", IngredientType.Verdura),
        Ingredient("Perejil", "https://example.com/perejil.jpg", IngredientType.Condimento),
        Ingredient("Pimienta", "https://example.com/Pimienta.jpg", IngredientType.Condimento),
        Ingredient("Pimiento", "https://example.com/pimiento.jpg", IngredientType.Verdura),
        Ingredient("Pimiento rojo", "https://example.com/pimiento-rojo.jpg", IngredientType.Verdura),
        Ingredient("Pimentón", "https://example.com/pimenton.jpg", IngredientType.Condimento),
        Ingredient("Pimentón dulce", "https://example.com/pimenton-dulce.jpg", IngredientType.Condimento),
        Ingredient("Proteína en polvo", "https://example.com/puerro.jpg", IngredientType.Otros),
        Ingredient("Puré de tomate", "https://example.com/pure-tomate.jpg", IngredientType.Verdura),
        Ingredient("Queso bajo en grasa", "https://example.com/queso-bajo.jpg", IngredientType.Lácteo),
        Ingredient("Queso magro rallado", "https://example.com/queso-magro.jpg", IngredientType.Lácteo),
        Ingredient("Queso rallado", "https://example.com/queso-rallado.jpg", IngredientType.Lácteo),
        Ingredient("Sal", "https://example.com/sal.jpg", IngredientType.Condimento),
        Ingredient("Salsa de soja baja en sodio", "https://example.com/salsa-soja.jpg", IngredientType.Condimento),
        Ingredient("Semillas de chía", "https://example.com/salsa-tomate.jpg", IngredientType.Verdura),
        Ingredient("Suprema de pollo", "https://example.com/suprema.jpg", IngredientType.Carne),
        Ingredient("Tapa de cuadril", "https://example.com/cuadril.jpg", IngredientType.Carne),
        Ingredient("Tomate", "https://example.com/tomate.jpg", IngredientType.Verdura),
        Ingredient("Tomate cherry", "https://example.com/tomate-cherry.jpg", IngredientType.Verdura),
        Ingredient("Tomate triturado", "https://example.com/tomate-triturado.jpg", IngredientType.Verdura),
        Ingredient("Tomillo", "https://example.com/tomillo.jpg", IngredientType.Condimento),
        Ingredient("Vinagre o jugo de limón", "https://example.com/vinagre.jpg", IngredientType.Líquido),
        Ingredient("Quinoa", "https://example.com/zanahoria.jpg", IngredientType.Verdura),
        Ingredient("Zanahoria", "https://example.com/zanahoria.jpg", IngredientType.Verdura)
    )

}