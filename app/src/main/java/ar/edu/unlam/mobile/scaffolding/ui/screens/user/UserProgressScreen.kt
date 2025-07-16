package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import ar.edu.unlam.mobile.scaffolding.ui.components.UserInfoHeader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProgressScreen(
    viewModel: UserProgressViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
) {
    val currentUser by viewModel.currentUser.collectAsState()

    val totalNutrition by viewModel.totalNutritionalValue.collectAsState()
    val todayNutrition by viewModel.todayNutritionalValue.collectAsState()
    Log.d("UserProgressScreen", "Recomposición UI - todayNutrition: $todayNutrition")

    val mostFrequentRecipeState by viewModel.mostFrequentRecipeState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
    ) {
        if (currentUser != null) {
            UserInfoHeader(user = currentUser!!)
            Spacer(modifier = Modifier.padding(6.dp))

            // Contenido principal de la pantalla
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Resumen Nutricional",
                    style = MaterialTheme.typography.bodyLarge,
                )
                HorizontalDivider(thickness = 1.dp)

                // --- SECCIÓN: RESUMEN DE HOY ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (todayNutrition != null) {
                        if (todayNutrition!!.calories > 0 || todayNutrition!!.protein > 0 || todayNutrition!!.carbs > 0 || todayNutrition!!.fats > 0 || todayNutrition!!.generalValue > 0) {
                            Log.d("UserProgressScreen", "MOSTRANDO NutritionalSummaryCard para HOY con datos: $todayNutrition")
                            NutritionalSummaryCard(
                                totalNutrition = todayNutrition!!,
                                title = "Nutrición Consumida Hoy",
                            )
                        } else {
                            Log.d(
                                "UserProgressScreen",
                                "Mostrando mensaje: 'Aún no has registrado consumo para hoy...' porque todayNutrition es: $todayNutrition",
                            )
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                Text(
                                    "Aún no has registrado consumo para hoy",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 7.dp),
                                )
                            }
                        }
                    } else {
                        Log.d("UserProgressScreen", "Mostrando: 'Calculando nutrición de hoy...' porque todayNutrition ES NULL")
                        Text("Calculando nutrición de hoy...")
                        CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                    }
                }

                // --- SECCIÓN: RESUMEN TOTAL ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (totalNutrition != null) {
                        NutritionalSummaryCard(totalNutrition = totalNutrition!!) // El title por defecto es "Resumen Nutricional Acumulado"
                    } else {
                        // Este Column interno está bien para centrar el texto y el indicador de carga
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Calculando valores nutricionales totales...",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator()
                        }
                    }
                }

                // --- SECCIÓN: RECETA MÁS FRECUENTE ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MostFrequentRecipeCard(uiState = mostFrequentRecipeState)
                }
                Spacer(modifier = Modifier.height(6.dp))
            } // Fin de la Columna de contenido principal
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        text = "Cargando datos del usuario...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionalSummaryCard(
    totalNutrition: NutritionalValue,
    title: String = "Resumen Nutricional Acumulado",
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp, vertical = 15.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            NutritionalDetailRow(label = "Calorías Totales:", value = "${totalNutrition.calories.roundToInt()} kcal")
            NutritionalDetailRow(label = "Proteínas Totales:", value = "${totalNutrition.protein.roundToInt()} g")
            NutritionalDetailRow(label = "Carbohidratos Totales:", value = "${totalNutrition.carbs.roundToInt()} g")
            NutritionalDetailRow(label = "Grasas Totales:", value = "${totalNutrition.fats.roundToInt()} g")
        }
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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun MostFrequentRecipeCard(uiState: MostFrequentRecipeUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        when {
            uiState.recipe != null -> {
                Column(
                    modifier =
                        Modifier
                            .padding(horizontal = 12.dp, vertical = 15.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Receta mas frecuente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically, // Centra la imagen y la columna de texto verticalmente
                    ) {
                        Image(
                            painter =
                                rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(data = uiState.recipe.imageUrl)
                                        .apply {
                                            crossfade(true)
                                            // placeholder(R.drawable.placeholder_image)
                                            // error(R.drawable.error_image)
                                        }.build(),
                                ),
                            contentDescription = "Imagen de ${uiState.recipe.name}",
                            modifier =
                                Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = uiState.recipe.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Cocinada ${uiState.count} ${if (uiState.count == 1) "vez" else "veces"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
            uiState.message != null -> {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 7.dp),
                )
            }
            else -> {
                CircularProgressIndicator()
            }
        }
    }
}
