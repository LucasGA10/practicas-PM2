package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.room.parser.expansion.ExpandableSection
import ar.edu.unlam.mobile.scaffolding.domain.model.user.DietGoal
import ar.edu.unlam.mobile.scaffolding.domain.model.user.Gender
import ar.edu.unlam.mobile.scaffolding.ui.components.ExpandableSection
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietFormScreen(
    viewModel: DietFormViewModel = hiltViewModel(),
    onSaveSuccess: () -> Unit = {}, // Callback para cuando se guarda exitosamente
    navController: NavHostController
) {
    val uiState = viewModel.uiState

    val onNavigateBack: (() -> Unit)? = {
        navController.popBackStack()
    }

    // Si el guardado fue exitoso, llama al callback y resetea el flag
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
            viewModel.onEvent(DietFormEvent.ResetSaveSuccess)
        }
    }

    // Para mostrar Snackbars de error
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = uiState.errorMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(DietFormEvent.ErrorMessageShown)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                title = "Formulario Dietapp",
                onNavigateBack = onNavigateBack, // Pasa la acción de retroceso
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize() // LazyColumn debe llenar el espacio
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // Padding horizontal para el contenido
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Sección de Información del Usuario ---
            item { Spacer(modifier = Modifier.height(16.dp)) } //Espacio entre los item
            item {
                AsyncImage(
                    model = uiState.userImageUrl,
                    contentDescription = "Imagen de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                )

            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Text(
                    text = uiState.userName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = "Ingresa tus datos físicos y objetivo",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- Campos del Formulario ---
            item {
                OutlinedTextField(
                    value = uiState.weight,
                    onValueChange = { viewModel.onEvent(DietFormEvent.WeightChanged(it)) },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.weight.isNotEmpty() && (uiState.weight.toFloatOrNull() == null || uiState.weight.toFloat() <= 0)
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                // Altura
                OutlinedTextField(
                    value = uiState.height,
                    onValueChange = { viewModel.onEvent(DietFormEvent.HeightChanged(it)) },
                    label = { Text("Altura (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                // Edad
                OutlinedTextField(
                    value = uiState.age,
                    onValueChange = { viewModel.onEvent(DietFormEvent.AgeChanged(it)) },
                    label = { Text("Edad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                // Género (Dropdown)
                GenderSelector(
                    selectedGender = uiState.selectedGender,
                    onGenderSelected = { viewModel.onEvent(DietFormEvent.GenderSelected(it)) }
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                // Objetivo de Dieta (Dropdown)
                DietGoalSelector(
                    selectedDietGoal = uiState.selectedDietGoal,
                    onDietGoalSelected = { viewModel.onEvent(DietFormEvent.DietGoalSelected(it)) }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }


            // --- Sección de Restricciones Dietéticas ---
            item {
                ExpandableSection(
                    title = "Restricciones Dietéticas (Opcional)",
                    isVisible = uiState.isDietarySectionVisible,
                    onHeaderClick = { viewModel.onEvent(DietFormEvent.ToggleDietarySection) }
                ) {
                    // Contenido de la sección expandible
                    Column(modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)) { // Añade padding interno si es necesario
                        uiState.dietaryRestrictions.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onEvent(
                                            DietFormEvent.RestrictionToggled(
                                                option.id
                                            )
                                        )
                                    }
                                    .padding(vertical = 8.dp, horizontal = 16.dp), // Padding para cada fila de checkbox
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = option.isSelected,
                                    onCheckedChange = { viewModel.onEvent(DietFormEvent.RestrictionToggled(option.id)) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) } // Espacio antes del botón

            // --- Botón de Guardar ---
            item {
                Button(
                    onClick = { viewModel.onEvent(DietFormEvent.SaveClicked) },
                    enabled = uiState.isFormValid && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) } // Espacio abajo
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderSelector(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val genders = Gender.values()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedGender?.name?: "Selecciona Género",
            onValueChange = {}, // No editable directamente
            readOnly = true,
            label = { Text("Género") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genders.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender.name) },
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietGoalSelector(
    selectedDietGoal: DietGoal?,
    onDietGoalSelected: (DietGoal) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dietGoals = DietGoal.values()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDietGoal?.name ?: "Selecciona Objetivo",
            onValueChange = {}, // No editable directamente
            readOnly = true,
            label = { Text("Objetivo de Dieta") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dietGoals.forEach { goal ->
                DropdownMenuItem(
                    text = { Text(goal.name) },
                    onClick = {
                        onDietGoalSelected(goal)
                        expanded = false
                    }
                )
            }
        }
    }
}