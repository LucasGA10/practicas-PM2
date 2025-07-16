package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import ar.edu.unlam.mobile.scaffolding.ui.components.UserInfoHeader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
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

    var showManualInputDialog by remember { mutableStateOf(false) }
    var manualCalories by remember { mutableStateOf("") }
    var manualProtein by remember { mutableStateOf("") }
    var manualCarbs by remember { mutableStateOf("") }
    var manualFats by remember { mutableStateOf("") }

    val weeklyChartData by viewModel.weeklyCaloriesChartData.collectAsState()

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
                                onAddClick = {
                                    Log.d("UserProgressScreen", "Botón '+' para añadir consumo manual clickeado!")
                                    manualCalories = ""
                                    manualProtein = ""
                                    manualCarbs = ""
                                    manualFats = ""
                                    showManualInputDialog = true
                                },
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
                // Diálogo para entrada manual de NutritionalValue
                if (showManualInputDialog) {
                    ManualNutritionalInputDialog(
                        onDismissRequest = { showManualInputDialog = false },
                        onConfirmClick = { calories, protein, carbs, fats ->
                            val customNutritionalValue =
                                NutritionalValue(
                                    calories = calories,
                                    protein = protein,
                                    carbs = carbs,
                                    fats = fats,
                                    // generalValue podría calcularse o dejarse en 0 si no es relevante para entradas manuales
                                )
                            Log.d("UserProgressScreen", "Confirmado: $customNutritionalValue")
                            viewModel.addCustomNutritionalValue(customNutritionalValue)
                            showManualInputDialog = false
                        },
                        calories = manualCalories,
                        onCaloriesChange = { manualCalories = it },
                        protein = manualProtein,
                        onProteinChange = { manualProtein = it },
                        carbs = manualCarbs,
                        onCarbsChange = { manualCarbs = it },
                        fats = manualFats,
                        onFatsChange = { manualFats = it },
                    )
                }

                // --- SECCIÓN: GRÁFICO SEMANAL DE CALORÍAS ---

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Column {
                        Text(
                            text = "Gráfico Semanal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (weeklyChartData != null && weeklyChartData!!.bars.isNotEmpty()) {
                            BarChart(
                                barChartData = weeklyChartData!!,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                xAxisDrawer =
                                    SimpleXAxisDrawer(
                                        axisLineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                yAxisDrawer =
                                    SimpleYAxisDrawer(
                                        axisLineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        labelTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        labelValueFormatter = { value -> "${value.toInt()}" },
                                    ),
                                labelDrawer =
                                    SimpleValueDrawer(
                                        drawLocation = SimpleValueDrawer.DrawLocation.Inside,
                                        labelTextColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                            )
                        } else {
                            // Estado de carga o vacío para el área del gráfico
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (weeklyChartData != null && weeklyChartData!!.bars.isEmpty()) {
                                    Text("No hay datos para mostrar esta semana.")
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator()
                                        Text(
                                            "Calculando datos del gráfico...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 8.dp),
                                        )
                                    }
                                }
                            }
                        }
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
    onAddClick: (() -> Unit)? = null, // Parámetro opcional para la acción del botón
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 15.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                // Mostrar el IconButton solo si onAddClick no es null
                if (onAddClick != null) {
                    IconButton(
                        onClick = onAddClick,
                        modifier =
                            Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .padding(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Añadir consumo manual",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp)) // Espacio después del título/botón

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
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
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
fun MostFrequentRecipeCard(
    uiState: MostFrequentRecipeUiState,
    navController: NavController = rememberNavController(),
) {
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
                            TextButton(
                                onClick = { navController.navigate("preparation/${uiState.recipe.id}") },
                                modifier = Modifier.align(Alignment.End),
                            ) {
                                Text(
                                    text = "Ver detalles",
                                    style = TextStyle(textDecoration = TextDecoration.Underline),
                                )
                            }
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

@Composable
fun ManualNutritionalInputDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: (calories: Float, protein: Float, carbs: Float, fats: Float) -> Unit,
    calories: String,
    onCaloriesChange: (String) -> Unit,
    protein: String,
    onProteinChange: (String) -> Unit,
    carbs: String,
    onCarbsChange: (String) -> Unit,
    fats: String,
    onFatsChange: (String) -> Unit,
) {
    var caloriesError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                "Añadir Consumo Manual",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp),
                ) {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = {
                            onCaloriesChange(it)
                            caloriesError = null
                        },
                        label = { Text("Calorías (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = caloriesError != null,
                        supportingText = { if (caloriesError != null) Text(caloriesError!!) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = protein,
                        onValueChange = onProteinChange,
                        label = { Text("Proteínas (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = onCarbsChange,
                        label = { Text("Carbohidratos (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = fats,
                        onValueChange = onFatsChange,
                        label = { Text("Grasas (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val cal = calories.toFloatOrNull()
                            val prot = protein.toFloatOrNull() ?: 0f
                            val carb = carbs.toFloatOrNull() ?: 0f
                            val fat = fats.toFloatOrNull() ?: 0f

                            if (cal == null) {
                                caloriesError = "Campo requerido o inválido"
                            } else {
                                caloriesError = null
                                onConfirmClick(cal, prot, carb, fat)
                            }
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Añadir")
                    }
                }
            }
        },
        // Vacío, manejamos los botones en 'text'
        confirmButton = {},
        dismissButton = {},
    )
}
