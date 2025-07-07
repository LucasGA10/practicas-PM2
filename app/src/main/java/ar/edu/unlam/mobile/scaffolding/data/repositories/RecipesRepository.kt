package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.Ingredient
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.IngredientType
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.UsedIngredient
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Category
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.data.repositories.IngredientsRepository.getIngredients
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

interface RecipesRepositoryInterface {
    fun getRecipeById(id: Int): Recipe?
    suspend fun updateRecipe(recipe: Recipe)
    fun getRecipes(): Flow<List<Recipe>>
}

@Singleton
class RecipesRepository
@Inject
constructor(): RecipesRepositoryInterface {
    private val recipes = mutableListOf<Recipe>()
    private val ingredients = getIngredients()

    private fun getIngredients(name: String): Ingredient {
        var ingredient = Ingredient("No se encotro el ingrediente", "https://thefoodtech.com/wp-content/uploads/2020/12/ingredientes-saludables.jpg", IngredientType.Otros)
        ingredients.forEach {
            if (it.name == name) {
                ingredient = it
            }
        }
        return ingredient
    }

    override fun getRecipeById(id: Int): Recipe? {
        return recipes.find { it.id == id }
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        val index = recipes.indexOfFirst { it.id == recipe.id }
        if (index != -1) {
            recipes[index] = recipe
        }
    }

    override fun getRecipes(): Flow<List<Recipe>> {
        return flowOf(recipes)
    }

    init {
        recipes.add(
            Recipe(
                id = 1,
                name = "Avena Proteica con Frutas",
                imageUrl = "https://tse4.mm.bing.net/th/id/OIP.xjYgFHrbD-GtOHv3fqYo-QHaEH?r=0&rs=1&pid=ImgDetMain&o=7&rm=3",
                category = Category.Desayuno,
                difficulty = Difficulty.Fácil,
                preparationTime = 5.0,
                cookingTime = 10.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Avena en hojuelas"), "1 taza"),
                    UsedIngredient(getIngredients("Leche vegetal"), "2 tazas"),
                    UsedIngredient(getIngredients("Proteína en polvo"), "1 scoop"),
                    UsedIngredient(getIngredients("Banana"), "1 unidad"),
                    UsedIngredient(getIngredients("Frutillas"), "1/2 taza"),
                    UsedIngredient(getIngredients("Semillas de chía"), "1 cucharadita"),
                    UsedIngredient(getIngredients("Miel o agave"), "1 cucharada")
                ),
                instructions = listOf(
                    "Calienta la leche vegetal en una olla a fuego medio.",
                    "Agrega la avena y cocina por 5-7 minutos, revolviendo constantemente.",
                    "Incorpora la proteína en polvo y mezcla bien.",
                    "Sirve en un bowl y decora con banana, frutillas y chía.",
                    "Añade miel si deseas un toque dulce."
                ),
                note = "Puedes usar otras frutas de temporada. Agrega canela para más sabor sin calorías.",
                tags = listOf(Category.Vegano, Category.Proteico, Category.Desayuno),
                nutritionalValue = 350)
        )
        recipes.add(
            Recipe(
                id = 2,
                name = "Ensalada de Quinoa con Garbanzos",
                imageUrl = "https://tse3.mm.bing.net/th/id/OIP.-ZPYUGpo7U0-v_ERH8VRsgHaFQ?r=0&rs=1&pid=ImgDetMain&o=7&rm=3",
                category = Category.Almuerzo,
                difficulty = Difficulty.Media,
                preparationTime = 15.0,
                cookingTime = 20.0,
                portions = 4,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Quinoa"), "1 taza"),
                    UsedIngredient(getIngredients("Garbanzos cocidos"), "1 taza"),
                    UsedIngredient(getIngredients("Pepino"), "1 unidad"),
                    UsedIngredient(getIngredients("Tomate cherry"), "1 taza"),
                    UsedIngredient(getIngredients("Palta"), "1 unidad"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "2 cucharadas"),
                    UsedIngredient(getIngredients("Jugo de limón"), "2 cucharadas"),
                    UsedIngredient(getIngredients("Sal"), "al gusto"),
                    UsedIngredient(getIngredients("Pimienta"), "al gusto"),
                    UsedIngredient(getIngredients("Perejil"), "2 cucharadas")
                ),
                instructions = listOf(
                    "Cocina la quinoa según las instrucciones del paquete y deja enfriar.",
                    "Corta el pepino, tomate cherry y palta en cubos.",
                    "Mezcla todos los ingredientes en un bowl grande.",
                    "Agrega aceite de oliva, jugo de limón, sal y pimienta.",
                    "Decora con perejil picado antes de servir."
                ),
                note = "Ideal para preparar con anticipación. Puedes cambiar los vegetales según disponibilidad.",
                tags = listOf(Category.Vegano, Category.Ligero, Category.Almuerzo, Category.Vegetariano),
                nutritionalValue = 420
            )
        )

        recipes.add(
            Recipe(
            id = 3,
            name = "Pollo al Horno con Verduras",
            imageUrl = "https://tse3.mm.bing.net/th/id/OIP.ThULyvoVe3r52fzWIuSXKAHaE8?r=0&rs=1&pid=ImgDetMain&o=7&rm=3",
            category = Category.Cena,
            difficulty = Difficulty.Fácil,
            preparationTime = 10.0,
            cookingTime = 35.0,
            portions = 3,
            usedIngredients = listOf(
                UsedIngredient(getIngredients("Pechugas de pollo"), "3 unidades"),
                UsedIngredient(getIngredients("Zanahoria"), "2 unidades"),
                UsedIngredient(getIngredients("Brócoli"), "1 taza"),
                UsedIngredient(getIngredients("Zapallito"), "1 unidad"),
                UsedIngredient(getIngredients("Aceite de oliva"), "1 cucharada"),
                UsedIngredient(getIngredients("Ajo en polvo"), "1 cucharadita"),
                UsedIngredient(getIngredients("Romero seco"), "1 cucharadita"),
                UsedIngredient(getIngredients("Sal"), "al gusto"),
                UsedIngredient(getIngredients("Pimienta"), "al gusto"),
            ),
            instructions = listOf(
                "Precalienta el horno a 200°C.",
                "Corta las verduras en trozos medianos.",
                "Coloca las pechugas y las verduras en una bandeja para horno.",
                "Rocía con aceite y espolvorea los condimentos.",
                "Hornea por 35 minutos o hasta que el pollo esté bien cocido."
            ),
            note = "Puedes usar muslos si prefieres carne más jugosa. Agrega batata para una versión más energética.",
            tags = listOf(Category.Proteico, Category.Horno, Category.Cena, Category.Vegetariano),
            nutritionalValue = 480
            )
        )
        recipes.add(
            Recipe(
                id = 4,
                name = "Tostadas de Palta con Huevo",
                imageUrl = "https://example.com/tostada-palta.jpg",
                category = Category.Desayuno,
                difficulty = Difficulty.Fácil,
                preparationTime = 10.0,
                cookingTime = 5.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Pan integral"), "2 rebanadas"),
                    UsedIngredient(getIngredients("Palta madura"), "1 unidad"),
                    UsedIngredient(getIngredients("Huevos"), "2 unidades"),
                    UsedIngredient(getIngredients("Sal"), "al gusto"),
                    UsedIngredient(getIngredients("Pimienta"), "al gusto"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "1 cucharadita")
                ),
                instructions = listOf(
                    "Tosta el pan en una sartén o tostadora.",
                    "Aplasta la palta con sal y pimienta.",
                    "Fríe los huevos en una sartén con un poco de aceite.",
                    "Unta la palta sobre las tostadas y coloca el huevo encima."
                ),
                note = "Podés añadir semillas o rodajas de tomate para más sabor y textura.",
                tags = listOf(Category.Desayuno, Category.Saludable, Category.Rápido, Category.Vegetariano),
                nutritionalValue = 320
            )
        )
        recipes.add(
            Recipe(
                id = 5,
                name = "Bolitas Energéticas de Avena",
                imageUrl = "https://example.com/bolitas-avena.jpg",
                category = Category.Snack,
                difficulty = Difficulty.Fácil,
                preparationTime = 15.0,
                portions = 6,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Avena arrollada"), "1 taza"),
                    UsedIngredient(getIngredients("Mantequilla de maní"), "1/2 taza"),
                    UsedIngredient(getIngredients("Miel"), "2 cucharadas"),
                    UsedIngredient(getIngredients("Cacao en polvo"), "1 cucharada"),
                    UsedIngredient(getIngredients("Chips de chocolate"), "1/4 taza"),
                    UsedIngredient(getIngredients("Semillas de chía"), "1 cucharada")
                ),
                instructions = listOf(
                    "Mezcla todos los ingredientes en un bowl grande.",
                    "Forma bolitas con la mezcla y colócalas en una bandeja.",
                    "Refrigera por al menos 30 minutos antes de consumir."
                ),
                note = "Conserva en la heladera por hasta 1 semana. Ideal como snack pre-entreno.",
                tags = listOf(Category.Snack, Category.Energético, Category.Vegano, Category.Vegetariano),
                nutritionalValue = 180
            )
        )
        recipes.add(
            Recipe(
                id = 6,
                name = "Wrap Vegetariano de Garbanzos",
                imageUrl = "https://example.com/wrap-garbanzos.jpg",
                category = Category.Almuerzo,
                difficulty = Difficulty.Media,
                preparationTime = 15.0,
                cookingTime = 5.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Tortillas integrales"), "2 unidades"),
                    UsedIngredient(getIngredients("Garbanzos cocidos"), "1 taza"),
                    UsedIngredient(getIngredients("Zanahoria rallada"), "1/2 taza"),
                    UsedIngredient(getIngredients("Hojas de espinaca"), "1 taza"),
                    UsedIngredient(getIngredients("Yogur natural o vegano"), "2 cucharadas"),
                    UsedIngredient(getIngredients("Ajo en polvo"), "1/2 cucharadita"),
                    UsedIngredient(getIngredients("Pimentón"), "al gusto"),
                    UsedIngredient(getIngredients("Sal"), "al gusto")
                ),
                instructions = listOf(
                    "Tritura ligeramente los garbanzos con tenedor.",
                    "Mezcla con yogur, ajo en polvo, pimentón y sal.",
                    "Calienta las tortillas si lo deseas.",
                    "Rellena con la mezcla, zanahoria y espinaca, y enrolla."
                ),
                note = "Podés usar hummus en lugar de yogur para una versión más sabrosa y 100% vegana.",
                tags = listOf(Category.Almuerzo, Category.Vegetariano, Category.Ligero),
                nutritionalValue = 390
            )
        )
        recipes.add(
            Recipe(
                id = 7,
                name = "Bowl de Vegetales y Tofu",
                imageUrl = "https://example.com/bowl-tofu.jpg",
                category = Category.Cena,
                difficulty = Difficulty.Media,
                preparationTime = 20.0,
                cookingTime = 15.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Tofu firme"), "200 g"),
                    UsedIngredient(getIngredients("Brócoli"), "1 taza"),
                    UsedIngredient(getIngredients("Zanahoria"), "1 unidad"),
                    UsedIngredient(getIngredients("Pimiento rojo"), "1 unidad"),
                    UsedIngredient(getIngredients("Salsa de soja baja en sodio"), "2 cucharadas"),
                    UsedIngredient(getIngredients("Ajo picado"), "1 diente"),
                    UsedIngredient(getIngredients("Arroz integral cocido"), "1 taza"),
                    UsedIngredient(getIngredients("Aceite de sésamo"), "1 cucharadita")
                ),
                instructions = listOf(
                    "Corta el tofu en cubos y saltéalo con aceite de sésamo hasta dorar.",
                    "Agrega el ajo, los vegetales en tiras y la salsa de soja.",
                    "Cocina a fuego medio hasta que los vegetales estén al dente.",
                    "Sirve sobre arroz integral caliente."
                ),
                note = "Podés cambiar el tofu por tempeh o agregar semillas de sésamo encima.",
                tags = listOf(Category.Vegano, Category.Vegetariano, Category.Cena, Category.Saludable, Category.SinGluten, Category.AltoEnFibra),
                nutritionalValue = 420
            )
        )
        recipes.add(
            Recipe(
                id = 8,
                name = "Batatas Asadas con Romero",
                imageUrl = "https://example.com/batatas-romero.jpg",
                category = Category.Cena,
                difficulty = Difficulty.Fácil,
                preparationTime = 10.0,
                cookingTime = 30.0,
                portions = 3,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Batatas"), "3 medianas"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "2 cucharadas"),
                    UsedIngredient(getIngredients("Romero seco"), "1 cucharadita"),
                    UsedIngredient(getIngredients("Sal marina"), "al gusto"),
                    UsedIngredient(getIngredients("Pimienta negra"), "al gusto")
                ),
                instructions = listOf(
                    "Precalienta el horno a 200°C.",
                    "Lava y corta las batatas en cubos con cáscara.",
                    "Mezcla con aceite, romero, sal y pimienta.",
                    "Hornea durante 30 minutos hasta que estén doradas y tiernas."
                ),
                note = "Quedan muy bien como acompañamiento o snack salado. También podés añadir pimentón dulce.",
                tags = listOf(Category.Vegano, Category.Horno, Category.Cena, Category.SinGluten, Category.Vegetariano),
                nutritionalValue = 300
            )
        )
        recipes.add(
            Recipe(
                id = 9,
                name = "Parfait de Yogur y Frutas",
                imageUrl = "https://example.com/parfait-frutas.jpg",
                category = Category.Postre,
                difficulty = Difficulty.Fácil,
                preparationTime = 10.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Yogur natural"), "1 taza"),
                    UsedIngredient(getIngredients("Frutas mixtas"), "1 taza"),
                    UsedIngredient(getIngredients("Granola"), "1/2 taza"),
                    UsedIngredient(getIngredients("Miel"), "1 cucharada"),
                    UsedIngredient(getIngredients("Semillas de chía"), "1 cucharadita")
                ),
                instructions = listOf(
                    "En un vaso, coloca una capa de yogur.",
                    "Agrega una capa de frutas, luego granola.",
                    "Repite las capas y finaliza con miel y chía por encima."
                ),
                note = "Para una opción vegana usá yogur vegetal y omití la miel o reemplazala por agave.",
                tags = listOf(Category.Postre, Category.Saludable, Category.Rápido, Category.Vegetariano, Category.SinAzúcar),
                nutritionalValue = 280
            )
        )
        recipes.add(
            Recipe(
                id = 10,
                name = "Ensalada de Pollo a la Parrilla",
                imageUrl = "https://example.com/ensalada-pollo.jpg",
                category = Category.Almuerzo,
                difficulty = Difficulty.Fácil,
                preparationTime = 15.0,
                cookingTime = 10.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Pechuga de pollo"), "1 unidad"),
                    UsedIngredient(getIngredients("Lechuga"), "2 tazas"),
                    UsedIngredient(getIngredients("Tomate cherry"), "1 taza"),
                    UsedIngredient(getIngredients("Pepino"), "1 unidad"),
                    UsedIngredient(getIngredients("Palta"), "1/2 unidad"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "1 cucharada"),
                    UsedIngredient(getIngredients("Jugo de limón"), "1 cucharada"),
                    UsedIngredient(getIngredients("Sal"), "al gusto"),
                    UsedIngredient(getIngredients("Pimienta"), "al gusto")
                ),
                instructions = listOf(
                    "Cocina el pollo a la plancha con sal y pimienta.",
                    "Corta las verduras y colócalas en un bowl.",
                    "Agrega el pollo en tiras, la palta, y el aderezo de aceite y limón.",
                    "Mezcla bien antes de servir."
                ),
                note = "Podés marinar el pollo con hierbas antes de cocinarlo para más sabor sin calorías extra.",
                tags = listOf(Category.Almuerzo, Category.Proteico, Category.Saludable, Category.SinGluten),
                nutritionalValue = 350
            )
        )
        recipes.add(
            Recipe(
                id = 11,
                name = "Salteado de Carne con Verduras",
                imageUrl = "https://example.com/salteado-carne.jpg",
                category = Category.Cena,
                difficulty = Difficulty.Media,
                preparationTime = 15.0,
                cookingTime = 15.0,
                portions = 3,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Carne magra (lomo o nalga)"), "300 g"),
                    UsedIngredient(getIngredients("Brócoli"), "1 taza"),
                    UsedIngredient(getIngredients("Zanahoria"), "1 unidad"),
                    UsedIngredient(getIngredients("Pimiento rojo"), "1 unidad"),
                    UsedIngredient(getIngredients("Salsa de soja"), "2 cucharadas"),
                    UsedIngredient(getIngredients("Ajo"), "1 diente"),
                    UsedIngredient(getIngredients("Aceite vegetal"), "1 cucharada")
                ),
                instructions = listOf(
                    "Corta la carne en tiras finas.",
                    "En una sartén caliente, saltea la carne con aceite y ajo.",
                    "Agrega las verduras cortadas y la salsa de soja.",
                    "Cocina por 10-15 minutos hasta que todo esté cocido pero crujiente."
                ),
                note = "Servilo solo o con arroz integral para una comida completa y balanceada.",
                tags = listOf(Category.Cena, Category.Proteico, Category.Saludable, Category.BajoEnCarbohidratos),
                nutritionalValue = 400
            )
        )
        recipes.add(
            Recipe(
                id = 12,
                name = "Medallones de Cerdo con Puré de Coliflor",
                imageUrl = "https://example.com/cerdo-coliflor.jpg",
                category = Category.Cena,
                difficulty = Difficulty.Media,
                preparationTime = 20.0,
                cookingTime = 25.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Medallones de lomo de cerdo"), "2 unidades"),
                    UsedIngredient(getIngredients("Coliflor"), "1 cabeza pequeña"),
                    UsedIngredient(getIngredients("Leche vegetal o descremada"), "1/4 taza"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "1 cucharada"),
                    UsedIngredient(getIngredients("Ajo en polvo"), "1 cucharadita"),
                    UsedIngredient(getIngredients("Perejil picado"), "1 cucharada"),
                    UsedIngredient(getIngredients("Sal"), "al gusto"),
                    UsedIngredient(getIngredients("Pimienta"), "al gusto")
                ),
                instructions = listOf(
                    "Hierve la coliflor hasta que esté blanda.",
                    "Procesa con leche, ajo en polvo, sal y pimienta hasta obtener un puré.",
                    "Cocina los medallones en una sartén con aceite, 4-5 minutos por lado.",
                    "Sirve los medallones sobre el puré, espolvorea con perejil."
                ),
                note = "Una alternativa más liviana al clásico puré de papas. Ideal para cenar sin pesadez.",
                tags = listOf(Category.Cena, Category.Saludable, Category.SinGluten, Category.BajoEnCarbohidratos),
                nutritionalValue = 370
            )
        )
        recipes.add(
            Recipe(
                id = 13,
                name = "Guiso de Lentejas",
                imageUrl = "https://example.com/guiso-lentejas.jpg",
                category = Category.Almuerzo,
                difficulty = Difficulty.Media,
                preparationTime = 15.0,
                cookingTime = 45.0,
                portions = 4,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Lentejas"), "1 taza"),
                    UsedIngredient(getIngredients("Carne picada magra"), "200 g"),
                    UsedIngredient(getIngredients("Zanahoria"), "1 unidad"),
                    UsedIngredient(getIngredients("Papas"), "1 unidad"),
                    UsedIngredient(getIngredients("Cebolla"), "1 unidad"),
                    UsedIngredient(getIngredients("Tomate triturado"), "1 taza"),
                    UsedIngredient(getIngredients("Pimentón dulce"), "1 cucharadita"),
                    UsedIngredient(getIngredients("Laurel"), "1 hoja"),
                    UsedIngredient(getIngredients("Sal y pimienta"), "al gusto"),
                    UsedIngredient(getIngredients("Agua"), "cantidad necesaria")
                ),
                instructions = listOf(
                    "Remojá las lentejas por al menos 4 horas (o usá lentejas rápidas).",
                    "Rehogá la cebolla y la carne picada en una olla.",
                    "Agregá el tomate, zanahoria, papa y condimentos.",
                    "Sumá las lentejas escurridas y cubrí con agua.",
                    "Cociná a fuego medio hasta que todo esté tierno y espeso."
                ),
                note = "Podés hacerlo sin carne para una versión vegetariana. Aporta mucha fibra y hierro.",
                tags = listOf(Category.Almuerzo, Category.Nutritivo, Category.Casero, Category.Saludable),
                nutritionalValue = 450
            )
        )
        recipes.add(
            Recipe(
                id = 14,
                name = "Fideos Integrales con Salsa de Tomate y Pollo",
                imageUrl = "https://example.com/fideos-pollo.jpg",
                category = Category.Almuerzo,
                difficulty = Difficulty.Fácil,
                preparationTime = 10.0,
                cookingTime = 20.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Fideos integrales"), "150 g"),
                    UsedIngredient(getIngredients("Pechuga de pollo"), "1 unidad"),
                    UsedIngredient(getIngredients("Tomate triturado"), "1 taza"),
                    UsedIngredient(getIngredients("Cebolla"), "1/2 unidad"),
                    UsedIngredient(getIngredients("Ajo"), "1 diente"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "1 cucharada"),
                    UsedIngredient(getIngredients("Orégano"), "1 cucharadita"),
                    UsedIngredient(getIngredients("Sal"), "al gusto"),
                    UsedIngredient(getIngredients("Pimienta"), "al gusto")
                ),
                instructions = listOf(
                    "Herví los fideos según las instrucciones del paquete.",
                    "Rehogá la cebolla y el ajo, luego agregá el pollo en cubos.",
                    "Una vez cocido, incorporá el tomate y el orégano.",
                    "Cociná la salsa unos minutos y mezclá con los fideos.",
                    "Serví caliente con un poco de queso rallado si querés."
                ),
                note = "Una forma de comer pasta más equilibrada. Ideal para deportistas o almuerzos familiares.",
                tags = listOf(Category.Almuerzo, Category.Casero, Category.Saludable, Category.Proteico),
                nutritionalValue = 480
            )
        )
        recipes.add(
            Recipe(
                id = 15,
                name = "Omelette de Verduras",
                imageUrl = "https://example.com/omelette-verduras.jpg",
                category = Category.Desayuno,
                difficulty = Difficulty.Fácil,
                preparationTime = 10.0,
                cookingTime = 5.0,
                portions = 1,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Huevos"), "2 unidades"),
                    UsedIngredient(getIngredients("Leche"), "2 cucharadas"),
                    UsedIngredient(getIngredients("Espinaca fresca"), "1/2 taza"),
                    UsedIngredient(getIngredients("Cebolla"), "1/4 unidad"),
                    UsedIngredient(getIngredients("Pimiento"), "1/4 unidad"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "1 cucharadita"),
                    UsedIngredient(getIngredients("Sal y pimienta"), "al gusto")
                ),
                instructions = listOf(
                    "Batí los huevos con la leche, sal y pimienta.",
                    "Rehogá las verduras en una sartén con aceite.",
                    "Verté la mezcla de huevo y cociná a fuego bajo.",
                    "Doblá por la mitad y cociná un minuto más. Serví caliente."
                ),
                note = "Ideal para arrancar el día con proteínas y fibra. Podés agregar queso bajo en grasa.",
                tags = listOf(Category.Desayuno, Category.Nutritivo, Category.Saludable, Category.Rápido),
                nutritionalValue = 270
            )
        )
        recipes.add(
            Recipe(
                id = 16,
                name = "Bife a la Plancha con Ensalada Criolla",
                imageUrl = "https://example.com/bife-ensalada.jpg",
                category = Category.Almuerzo,
                difficulty = Difficulty.Fácil,
                preparationTime = 10.0,
                cookingTime = 10.0,
                portions = 1,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Bife de carne magra"), "1 unidad"),
                    UsedIngredient(getIngredients("Tomate"), "1 unidad"),
                    UsedIngredient(getIngredients("Cebolla"), "1/2 unidad"),
                    UsedIngredient(getIngredients("Morrón rojo"), "1/4 unidad"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "1 cucharada"),
                    UsedIngredient(getIngredients("Vinagre o jugo de limón"), "1 cucharada"),
                    UsedIngredient(getIngredients("Sal"), "al gusto"),
                    UsedIngredient(getIngredients("Pimienta"), "al gusto")
                ),
                instructions = listOf(
                    "Cociná el bife a la plancha con apenas aceite, 4-5 minutos por lado.",
                    "Picá los vegetales para la ensalada criolla.",
                    "Condimentá con aceite, vinagre, sal y pimienta.",
                    "Serví todo junto, preferentemente con poca sal para cuidar la presión."
                ),
                note = "Una opción clásica, rápida y nutritiva. Usá cortes magros y cocción sin grasa.",
                tags = listOf(Category.Almuerzo, Category.Argentina, Category.Saludable, Category.Casero),
                nutritionalValue = 430
            )
        )
        recipes.add(
            Recipe(
                id = 17,
                name = "Humita Light en Olla",
                imageUrl = "https://example.com/humita-olla.jpg",
                category = Category.Cena,
                difficulty = Difficulty.Media,
                preparationTime = 15.0,
                cookingTime = 30.0,
                portions = 3,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Choclos frescos"), "3 unidades"),
                    UsedIngredient(getIngredients("Cebolla"), "1 unidad"),
                    UsedIngredient(getIngredients("Leche descremada"), "1/2 taza"),
                    UsedIngredient(getIngredients("Ají molido"), "1 cucharadita"),
                    UsedIngredient(getIngredients("Pimienta"), "al gusto"),
                    UsedIngredient(getIngredients("Queso magro rallado"), "50 g"),
                    UsedIngredient(getIngredients("Aceite vegetal"), "1 cucharadita")
                ),
                instructions = listOf(
                    "Desgraná los choclos y procesalos con un poco de leche.",
                    "Rehogá la cebolla, agregá la mezcla de choclo y cociná lentamente.",
                    "Condimentá, agregá el queso y cociná hasta espesar.",
                    "Serví caliente en cazuela o con ensalada verde."
                ),
                note = "Versión más liviana de la tradicional. Ideal para invierno o como plato único vegetariano.",
                tags = listOf(Category.Cena, Category.Argentina, Category.Saludable, Category.Vegetariano),
                nutritionalValue = 380
            )
        )
        recipes.add(
            Recipe(
                id = 18,
                name = "Revuelto Gramajo Saludable",
                imageUrl = "https://example.com/revuelto-gramajo.jpg",
                category = Category.Cena,
                difficulty = Difficulty.Media,
                preparationTime = 15.0,
                cookingTime = 15.0,
                portions = 2,
                usedIngredients = listOf(
                    UsedIngredient(getIngredients("Papas"), "2 medianas"),
                    UsedIngredient(getIngredients("Huevos"), "3 unidades"),
                    UsedIngredient(getIngredients("Cebolla"), "1 unidad"),
                    UsedIngredient(getIngredients("Jamón cocido natural"), "100 g"),
                    UsedIngredient(getIngredients("Aceite de oliva"), "1 cucharada"),
                    UsedIngredient(getIngredients("Perejil"), "al gusto"),
                    UsedIngredient(getIngredients("Sal"), "al gusto"),
                    UsedIngredient(getIngredients("Pimienta"), "al gusto")
                ),
                instructions = listOf(
                    "Herví o cociná al horno las papas hasta que estén blandas, luego cortalas en cubitos.",
                    "Rehogá la cebolla con aceite, agregá el jamón en tiras.",
                    "Agregá las papas y salteá todo junto.",
                    "Incorporá los huevos batidos y mezclá hasta cuajar.",
                    "Espolvoreá con perejil y serví caliente."
                ),
                note = "Usar papas al horno en lugar de fritas reduce significativamente las grasas. Rico y más liviano.",
                tags = listOf(Category.Cena, Category.Argentina, Category.Saludable, Category.Casero),
                nutritionalValue = 410
            )
        )

    }

}