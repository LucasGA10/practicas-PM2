package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import ar.edu.unlam.mobile.scaffolding.ui.components.ClickableRatingBar
import ar.edu.unlam.mobile.scaffolding.ui.components.RatingBar
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import coil.compose.AsyncImage

@Composable
fun PreparationScreen(
    viewModel: PreparationViewModel = hiltViewModel(),
    navController: NavController,
) {
    val recipe by viewModel.recipe.collectAsState()
    val uiUsedIngredients: List<UiUsedIngredient> by viewModel.uiUsedIngredients.collectAsState()

    var ingredientsVisible by remember { mutableStateOf(true) }

    val back: (() -> Unit)? = {
        navController.popBackStack()
    }

    var userSelectedRating by remember(recipe?.rating) {
        mutableFloatStateOf(recipe?.rating ?: 0f)
    }
    val onSubmitRating: (Int, Float) -> Unit = { recipeId, newRating ->
        viewModel.updateRecipeRating(recipeId, newRating)
    }

    Scaffold(
        topBar = { TopBar("Preparación", back) },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            item {
                Box( // Box principal para la cabecera de la imagen
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Altura del contenedor general
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    AsyncImage(
                        model = recipe?.imageUrl,
                        contentDescription = "Fondo difuminado de la receta", // Descripción diferente
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .blur(radius = 24.dp),
                        // Ajusta el radio del blur
                        contentScale = ContentScale.Crop, // Recorta para llenar
                    )
                    AsyncImage(
                        model = recipe?.imageUrl,
                        contentDescription = recipe?.name,
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.3f)) // Fondo semitransparente para legibilidad
                                .align(Alignment.BottomCenter) // Alinea este Box en la parte inferior del Box padre
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = recipe?.name ?: "Cargando título...",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier =
                                Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(end = 48.dp)
                                    .padding(bottom = 7.dp),
                        )
                        // Botón de Corazón (Favorito)
                        recipe?.let { recipe -> // Solo muestra el botón si la receta está cargada
                            IconButton(
                                onClick = { viewModel.toggleFavorite(recipe.id) }, // Llama a la función del ViewModel
                                modifier =
                                    Modifier
                                        .align(Alignment.CenterEnd) // Corazón a la derecha
                                        .size(40.dp), // Tamaño del área clickeable del botón
                            ) {
                                Icon(
                                    imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = if (recipe.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                                    tint = if (recipe.isFavorite) Color.Red else Color.White, // Color del corazón
                                    modifier = Modifier.size(28.dp), // Tamaño del icono en sí
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Porciones (a la izquierda)
                        recipe?.portions?.let { porciones ->
                            Text(
                                text = "Porciones: $porciones",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } ?: Spacer(modifier = Modifier.weight(1f))

                        // Rating (a la derecha)
                        recipe?.rating?.let { ratingValue ->
                            RatingBar(
                                rating = ratingValue,
                                starSize = 20.dp,
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Lista de ingredientes
            item {
                ExpandableSection(
                    title = "Ingredientes (${uiUsedIngredients.size})",
                    isVisible = ingredientsVisible,
                    onHeaderClick = { ingredientsVisible = !ingredientsVisible },
                ) {
                    if (uiUsedIngredients.isNotEmpty()) {
                        Log.d("PrepScreen", "Hay ${uiUsedIngredients.size} UiUsedIngredients para mostrar.")
                        val usedIngredientPairs = uiUsedIngredients.chunked(2)

                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            usedIngredientPairs.forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    // Card para el primer ingrediente del par
                                    Box(modifier = Modifier.weight(1f)) {
                                        IngredientCard(uiUsedIngredient = pair[0])
                                    }

                                    // Card para el segundo ingrediente del par (si existe)
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (pair.size > 1) {
                                            IngredientCard(uiUsedIngredient = pair[1])
                                        } else {
                                            Spacer(Modifier.fillMaxWidth())
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d("PrepScreen", "uiStateUsedIngredients está VACÍO. Mostrando 'No hay ingredientes disponibles'.")
                        Text(
                            "No hay ingredientes disponibles.",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                }
            }

            // Usamos un ElevatedCard para un estilo similar, o puedes replicar tu Column de "Consejo"
            if (recipe?.nutritionalValue != null) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    NutritionalInfoCard(nutritionalValue = recipe?.nutritionalValue)
                }
            }

            // Tarjeta de Consejo
            recipe?.note?.takeIf { it.isNotBlank() }?.let { tipText ->
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f))
                                .padding(all = 12.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Icono de bombilla
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = "Consejo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Consejo:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = tipText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Encabezado para la sección de Pasos de Preparación
            item {
                Text(
                    text = "Pasos de Preparación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            // Lista de Pasos de Preparación
            val steps = recipe?.instructions
            if (steps.isNullOrEmpty()) {
                item {
                    Text(
                        text = "No hay instrucciones disponibles.",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }
            } else {
                itemsIndexed(steps) { index, instruction ->
                    // Columna para cada paso completo, para aplicar padding y la línea divisoria
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                // AUMENTAR AQUÍ LA SEPARACIÓN VERTICAL ENTRE PASOS
                                .padding(vertical = 14.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                        ) {
                            // Caja para el número del paso
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier =
                                    Modifier
                                        .defaultMinSize(minWidth = 70.dp) // Asegura un ancho mínimo para "Paso XX"
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.tertiary)
                                        .padding(horizontal = 8.dp, vertical = 6.dp), // Padding interno de la caja del número
                            ) {
                                Text(
                                    text = "Paso ${index + 1}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = instruction,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        // Línea divisoria, no se muestra después del último ítem
                        if (index < steps.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 16.dp), // Aumenta el espacio sobre la línea divisoria
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Puntuación de la receta
            item {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "¿Qué te pareció la receta?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    // Estrellas clickeables para nuevo rating
                    ClickableRatingBar(
                        currentRating = userSelectedRating,
                        onRatingChanged = { newRating ->
                            userSelectedRating = newRating
                            recipe?.id?.let { id ->
                                onSubmitRating(id, newRating)
                            }
                        },
                        starSize = 36.dp,
                    )

                    // Opcional: Botón para confirmar el rating
                    // Si prefieres que el usuario confirme antes de enviar:
                    /*
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            recipe?.id?.let { id ->
                                onSubmitRating(id, userSelectedRating)
                            }
                        },
                        enabled = recipe != null && userSelectedRating > 0f // Habilita si hay rating
                    ) {
                        Text("Enviar Calificación")
                    }
                     */
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) } // Espacio al final
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    isVisible: Boolean,
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit, // Contenido de la sección como un slot Composable
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onHeaderClick) // Hace que toda la fila del encabezado sea clickeable
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            // Padding para el encabezado
            horizontalArrangement = Arrangement.SpaceBetween, // Separa el título del ícono
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                // modifier = Modifier.weight(1f) // Opcional: si quieres que el texto ocupe más espacio
            )
            Icon(
                imageVector = if (isVisible) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isVisible) "Colapsar $title" else "Expandir $title",
                tint = MaterialTheme.colorScheme.primary, // Opcional: para darle color al ícono
            )
        }

        // AnimatedVisibility para mostrar/ocultar el contenido con animación
        AnimatedVisibility(
            visible = isVisible,
            // Modifica aquí la duración de las animaciones
            enter =
                fadeIn(animationSpec = tween(durationMillis = 100)) +
                    slideInVertically(
                        animationSpec = tween(durationMillis = 150),
                        initialOffsetY = { fullHeight -> -fullHeight / 4 },
                    ),
            exit =
                fadeOut(animationSpec = tween(durationMillis = 100)) +
                    slideOutVertically(
                        animationSpec = tween(durationMillis = 150),
                        targetOffsetY = { fullHeight -> -fullHeight / 4 },
                    ),
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun IngredientCard(uiUsedIngredient: UiUsedIngredient) {
    // Puedes usar Card o ElevatedCard para diferentes efectos visuales
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Sombra para ElevatedCard
    ) {
        Column {
            AsyncImage(
                model = uiUsedIngredient.imageUrl,
                contentDescription = "Imagen de ${uiUsedIngredient.name}",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop,
            )
            Column(Modifier.padding(all = 8.dp)) {
                Text(
                    text = uiUsedIngredient.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tipo: ${uiUsedIngredient.typeName}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cantidad: ${uiUsedIngredient.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun NutritionalInfoCard(nutritionalValue: NutritionalValue?) {
    if (nutritionalValue == null) {
        // No mostrar nada si no hay datos nutricionales
        return
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp) // Padding lateral para la caja, igual que el consejo
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)) // Fondo similar al consejo
                .padding(all = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.RestaurantMenu,
                contentDescription = "Información Nutricional",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Información Nutricional (por porción):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar cada valor nutricional si está disponible
        NutritionalDetailRow("Valor General:", "${nutritionalValue.generalValue}")
        NutritionalDetailRow("Calorías:", "${nutritionalValue.calories} kcal")
        NutritionalDetailRow("Proteínas:", "${nutritionalValue.protein} g")
        NutritionalDetailRow("Carbohidratos:", "${nutritionalValue.carbs} g")
        NutritionalDetailRow("Grasas:", "${nutritionalValue.fats} g")
    }
}

@Composable
fun NutritionalDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
