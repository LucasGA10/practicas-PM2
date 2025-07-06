package ar.edu.unlam.mobile.scaffolding.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.UsedIngredient
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
    var ingredientsVisible by remember { mutableStateOf(true) }

    val back: (() -> Unit)? = {
        navController.popBackStack()
    }

    val onToggleFavorite: (Int) -> Unit = { recipeId ->
        viewModel.toggleFavorite(recipeId) // Asume que tienes un ID en tu receta
    }

    var userSelectedRating by remember(recipe?.rating) {
        mutableFloatStateOf(recipe?.rating ?: 0f)
    }
    val onSubmitRating: (Int, Float) -> Unit = { recipeId, newRating ->
        viewModel.updateRecipeRating(recipeId , newRating)
    }

    Scaffold(
        topBar = { TopBar("Preparación", back) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Box( /* ... tu Box con AsyncImage y Text para el título ... */) {
                    AsyncImage(
                        model = recipe?.imageUrl,
                        contentDescription = null,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .size(200.dp)
                                .background(MaterialTheme.colorScheme.secondary),
                        )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f)) // Fondo semitransparente para legibilidad
                            .align(Alignment.BottomCenter) // Alinea este Box en la parte inferior del Box padre
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = recipe?.name?: "Cargando título...",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomStart) // Título a la izquierda
                                .padding(end = 48.dp) // Deja espacio para el botón de corazón
                        )

                        // Botón de Corazón (Favorito)
                        recipe?.let { recipe -> // Solo muestra el botón si la receta está cargada
                            IconButton(
                                onClick = { onToggleFavorite(recipe.id) }, // Llama a la función del ViewModel
                                modifier = Modifier
                                    .align(Alignment.CenterEnd) // Corazón a la derecha
                                    .size(40.dp) // Tamaño del área clickeable del botón
                            ) {
                                Icon(
                                    imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = if (recipe.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                                    tint = if (recipe.isFavorite) Color.Red else Color.White, // Color del corazón
                                    modifier = Modifier.size(28.dp) // Tamaño del icono en sí
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) } // Espacio antes de esta sección

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, // Esto es clave
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Porciones (a la izquierda)
                        recipe?.portions?.let { porciones ->
                            Text(
                                text = "Porciones: $porciones",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        } ?: Spacer(modifier = Modifier.weight(1f)) // Ocupa espacio si no hay porciones para mantener el rating a la derecha


                        // Rating (a la derecha)
                        recipe?.rating?.let { ratingValue ->
                            RatingBar(
                                rating = ratingValue,
                                starsColor = MaterialTheme.colorScheme.primary,
                                starSize = 20.dp
                            )
                        }
                    }
                    // Opcional: una línea divisora sutil debajo
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                ExpandableSection(
                    title = "Ingredientes",
                    isVisible = ingredientsVisible,
                    onHeaderClick = { ingredientsVisible = !ingredientsVisible }
                ) {
                    recipe?.usedIngredients?.takeIf { it.isNotEmpty() }
                        ?.let { currentUsedIngredients ->
                            val usedIngredientPairs = currentUsedIngredients.chunked(2)

                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 8.dp
                                ), // Ajusta padding si es necesario
                                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre filas de Cards
                            ) {
                                usedIngredientPairs.forEach { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre Cards en una fila
                                    ) {
                                        // Card para el primer ingrediente del par
                                        Box(modifier = Modifier.weight(1f)) {
                                            IngredientCard(usedIngredient = pair[0])
                                        }

                                        // Card para el segundo ingrediente del par (si existe)
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (pair.size > 1) {
                                                IngredientCard(usedIngredient = pair[1])
                                            } else {
                                                Spacer(Modifier.fillMaxWidth()) // Mantiene la estructura si solo hay un item
                                            }
                                        }
                                    }
                                }
                            }
                        } ?: Text(
                        "No hay ingredientes disponibles.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            recipe?.note?.takeIf { it.isNotBlank() }?.let { tipText ->
                item { Spacer(modifier = Modifier.height(16.dp)) } // Espacio antes del consejo

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp) // Padding lateral para la caja del consejo
                            .clip(RoundedCornerShape(8.dp)) // Esquinas redondeadas para la caja
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)) // Fondo gris claro
                            .padding(all = 12.dp) // Padding interno de la caja
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb, // Icono de bombilla
                                contentDescription = "Consejo",
                                tint = MaterialTheme.colorScheme.primary, // Color del icono
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Consejo:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = tipText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) } // Espacio después del consejo
            }

            // Encabezado para la sección de Pasos de Preparación
            item {
                Text(
                    text = "Pasos de Preparación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Lista de Pasos de Preparación
            val steps = recipe?.instructions
            if (steps.isNullOrEmpty()) {
                item {
                    Text(
                        text = "No hay instrucciones disponibles.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            } else {
                itemsIndexed(steps) { index, instruction ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) { // Padding horizontal para toda la fila del paso
                        Row(
                            verticalAlignment = Alignment.Top, // Alinear el número y el texto en la parte superior
                            modifier = Modifier.padding(vertical = 8.dp) // Padding vertical para cada paso
                        ) {
                            // Caja para el número del paso
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp) // Tamaño de la caja del número, ajusta según necesidad
                                    .clip(RoundedCornerShape(8.dp)) // Esquinas redondeadas (o CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant) // Color gris claro
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp // Tamaño del número
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp)) // Espacio entre el número y el texto

                            Text(
                                text = instruction,
                                style = MaterialTheme.typography.bodyLarge, // Texto más grande para la instrucción
                                // fontSize = 18.sp, // Alternativamente, puedes forzar un tamaño específico
                                modifier = Modifier.weight(1f) // Para que el texto ocupe el espacio restante
                            )
                        }
                        // Línea divisoria, no se muestra después del último ítem
                        if (index < steps.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp), // Espacio sobre la línea divisoria
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f) // Color de la línea, más sutil
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) } // Más espacio antes de esta sección

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Centra el contenido de la columna
                ) {
                    Text(
                        text = "¿Qué te pareció la receta?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Estrellas clickeables para nuevo rating
                    ClickableRatingBar(
                        currentRating = userSelectedRating,
                        onRatingChanged = { newRating ->
                            userSelectedRating = newRating
                            // Opcionalmente, puedes enviar el rating inmediatamente al hacer clic
                            // o esperar a que el usuario presione un botón de "Enviar".
                            // Por ahora, solo actualizamos el estado local.
                            // Si quieres enviar inmediatamente:
                            recipe?.id?.let { id ->
                                onSubmitRating(id, newRating)
                            }
                        },
                        starSize = 36.dp // Estrellas un poco más grandes para facilitar el clic
                    )

                    // Opcional: Botón para confirmar el rating
                    // Si prefieres que el usuario confirme antes de enviar:
                    /*
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            recipeDetails?.id?.let { id ->
                                onSubmitRating(id, userSelectedRating)
                            }
                        },
                        enabled = recipeDetails != null && userSelectedRating > 0f // Habilita si hay rating
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
    content: @Composable () -> Unit // Contenido de la sección como un slot Composable
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onHeaderClick) // Hace que toda la fila del encabezado sea clickeable
                .padding(vertical = 12.dp, horizontal = 16.dp), // Padding para el encabezado
            horizontalArrangement = Arrangement.SpaceBetween, // Separa el título del ícono
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
                // modifier = Modifier.weight(1f) // Opcional: si quieres que el texto ocupe más espacio
            )
            Icon(
                imageVector = if (isVisible) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isVisible) "Colapsar $title" else "Expandir $title",
                tint = MaterialTheme.colorScheme.primary // Opcional: para darle color al ícono
            )
        }

        // Línea divisora opcional debajo del encabezado
        // if (isVisible) { // Podrías mostrarla solo si está expandida, o siempre
        //     Divider(modifier = Modifier.padding(horizontal = 16.dp))
        // }

        // AnimatedVisibility para mostrar/ocultar el contenido con animación
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(animationSpec = tween(durationMillis = 300)) { fullHeight -> -fullHeight / 4 },
            exit = fadeOut(animationSpec = tween(durationMillis = 200)) + slideOutVertically(animationSpec = tween(durationMillis = 300)) { fullHeight -> -fullHeight / 4 }
        ) {
            // El contenido se pasa como un lambda, lo que permite cualquier Composable aquí.
            // Se envuelve en una Column para asegurar que el padding se aplique correctamente
            // y el contenido tenga un contenedor si AnimatedVisibility necesita medirlo.
            Column {
                content()
            }
        }
    }
}

@Composable
fun IngredientCard(usedIngredient: UsedIngredient) {
    // Puedes usar Card o ElevatedCard para diferentes efectos visuales
    ElevatedCard( // O Card()
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Sombra para ElevatedCard
    ) {
        Column {
            AsyncImage(
                model = usedIngredient.ingredient.imageUrl,
                contentDescription = "Imagen de ${usedIngredient.ingredient.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f), // O la relación de aspecto que prefieras para la imagen
                contentScale = ContentScale.Crop // Para que la imagen cubra el área asignada
            )
            Column(Modifier.padding(all = 8.dp)) {
                Text(
                    text = usedIngredient.ingredient.name,
                    style = MaterialTheme.typography.titleSmall, // O titleMedium
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // Por si el nombre es muy largo
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tipo: ${usedIngredient.ingredient.type}", // Asumiendo que type es un String o Enum.toString()
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cantidad: ${usedIngredient.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}