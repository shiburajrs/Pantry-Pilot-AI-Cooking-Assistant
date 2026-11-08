package com.pantrypilot.app.ui.screens.review_ingredients

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.pantrypilot.app.R
import com.pantrypilot.app.data.model.Ingredient
import com.pantrypilot.app.ui.components.CircularLoadingIndicator
import com.pantrypilot.app.ui.components.PantryPilotAppBar
import com.pantrypilot.app.ui.components.PantryPilotPrimaryButton
import com.pantrypilot.app.ui.theme.PantryBackground
import com.pantrypilot.app.ui.theme.PantryFavorite
import com.pantrypilot.app.ui.theme.PantryOrange
import com.pantrypilot.app.ui.theme.PantryOrangeLight2
import com.pantrypilot.app.ui.theme.PantrySurface
import com.pantrypilot.app.ui.theme.PantryTextPrimary
import com.pantrypilot.app.ui.theme.PantryTextSecondary
import com.pantrypilot.app.util.AppConstants
import com.pantrypilot.app.util.ImageCompressor
import java.io.File

@Composable
fun ReviewIngredientsScreen(
    imagePath: String? = null,
    onBackClick: () -> Unit = {},
    onContinueClick: (List<Ingredient>) -> Unit,
    viewModel: ReviewIngredientViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var ingredients by remember { mutableStateOf(uiState.ingredients) }
    var editingIngredientIndex by remember { mutableStateOf<Int?>(null) }
    var isAddingIngredient by remember { mutableStateOf(false) }

    LaunchedEffect(imagePath) {
        if (!imagePath.isNullOrBlank()) {
            val file = File(imagePath)
            if (file.exists()) {
                viewModel.detectIngredients(file)
            }
        }
    }

    LaunchedEffect(uiState.ingredients) {
        if (uiState.ingredients.isNotEmpty()) {
            ingredients = uiState.ingredients
        }
    }

    val imageBitmap = remember(imagePath) {
        imagePath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                ImageCompressor.decodeSampledBitmap(file, maxDimension = 720)?.asImageBitmap()
            } else {
                null
            }
        }
    }

    Scaffold(
        topBar = {
            PantryPilotAppBar(
                title = stringResource(R.string.review_ingredients_title),
                onBackClick = onBackClick
            )
        },
        containerColor = PantryBackground
    ) { innerPadding ->

        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularLoadingIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.review_analyzing_loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = PantryTextSecondary
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 90.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (imageBitmap != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = stringResource(R.string.cd_captured_image_review),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PantrySurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(PantryOrangeLight2),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestaurantMenu,
                                        contentDescription = null,
                                        tint = PantryOrange,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.review_add_ingredients_header),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PantryTextPrimary
                                    )
                                    Text(
                                        text = stringResource(R.string.review_add_ingredients_desc),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PantryTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    if (!uiState.errorMessage.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFDE8E8)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.detection_issue_title),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearError() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.cd_dismiss_error),
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = uiState.errorMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PantryTextPrimary
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.retryDetection() },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.action_retry_scan), fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { isAddingIngredient = true },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.action_add_manually), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.review_detected_ingredients_count, ingredients.size),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PantryTextPrimary,
                            fontSize = 17.sp
                        )

                        if(imageBitmap != null && ingredients.isNotEmpty())

                        OutlinedButton(
                            onClick = { isAddingIngredient = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PantryOrange
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.action_add_item), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (ingredients.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PantrySurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (uiState.isSuccess) stringResource(R.string.review_no_ingredients_photo) else stringResource(R.string.review_no_ingredients_listed),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PantryTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (uiState.isSuccess)
                                        stringResource(R.string.review_no_ingredients_photo_hint)
                                    else
                                        stringResource(R.string.review_no_ingredients_manual_hint),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PantryTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                OutlinedButton(
                                    onClick = { isAddingIngredient = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = PantryOrange
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.action_add_first_ingredient), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ingredients.forEachIndexed { index, item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = PantrySurface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IngredientImage(
                                            name = item.name,
                                            modifier = Modifier.size(60.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = PantryTextPrimary,
                                                fontSize = 15.sp
                                            )

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (item.quantity.isNotBlank()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = PantryOrangeLight2
                                                    ) {
                                                        Text(
                                                            text = item.quantity,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            fontSize = 11.sp,
                                                            color = PantryOrange,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }

                                                if (item.category.isNotBlank()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = Color(0xFFF0EAE4)
                                                    ) {
                                                        Text(
                                                            text = item.category,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            fontSize = 11.sp,
                                                            color = PantryTextSecondary,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        IconButton(
                                            onClick = { editingIngredientIndex = index },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = stringResource(R.string.cd_edit_ingredient),
                                                tint = PantryOrange,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val list = ingredients.toMutableList()
                                                list.removeAt(index)
                                                ingredients = list
                                                viewModel.updateIngredients(list)
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = stringResource(R.string.cd_remove_ingredient),
                                                tint = PantryFavorite,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }


                }

                PantryPilotPrimaryButton(
                    text = stringResource(R.string.action_find_recipes_count, ingredients.size),
                    onClick = { onContinueClick(ingredients) },
                    enabled = ingredients.isNotEmpty(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 14.dp)
                )
            }
        }
    }

    if (editingIngredientIndex != null) {
        val index = editingIngredientIndex!!
        val ingredient = ingredients.getOrNull(index)
        if (ingredient != null) {
            EditIngredientDialog(
                initialIngredient = ingredient,
                onDismiss = { editingIngredientIndex = null },
                onSave = { updated ->
                    val list = ingredients.toMutableList()
                    list[index] = updated
                    ingredients = list
                    viewModel.updateIngredients(list)
                    editingIngredientIndex = null
                },
                onDelete = {
                    val list = ingredients.toMutableList()
                    list.removeAt(index)
                    ingredients = list
                    viewModel.updateIngredients(list)
                    editingIngredientIndex = null
                }
            )
        }
    }

    if (isAddingIngredient) {
        AddIngredientDialog(
            onDismiss = { isAddingIngredient = false },
            onAdd = { newIngredient ->
                val updatedList = ingredients + newIngredient
                ingredients = updatedList
                viewModel.updateIngredients(updatedList)
                isAddingIngredient = false
            }
        )
    }
}

@Composable
fun EditIngredientDialog(
    initialIngredient: Ingredient,
    onDismiss: () -> Unit,
    onSave: (Ingredient) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(initialIngredient.name) }
    var quantity by remember { mutableStateOf(initialIngredient.quantity) }
    var category by remember { mutableStateOf(initialIngredient.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_ingredient_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.dialog_field_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(stringResource(R.string.dialog_field_quantity)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(stringResource(R.string.dialog_field_category)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            initialIngredient.copy(
                                name = name.trim(),
                                quantity = quantity.trim(),
                                category = category.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PantryOrange)
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text(stringResource(R.string.action_delete), color = PantryFavorite)
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        }
    )
}

@Composable
fun AddIngredientDialog(
    onDismiss: () -> Unit,
    onAdd: (Ingredient) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_add_ingredient_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.dialog_field_name_required)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(stringResource(R.string.dialog_field_quantity_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(stringResource(R.string.dialog_field_category_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            Ingredient(
                                name = name.trim(),
                                confidence = 1.0f,
                                quantity = quantity.trim().ifEmpty { "1 serving" },
                                category = category.trim().ifEmpty { "Pantry" }
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PantryOrange)
            ) {
                Text(stringResource(R.string.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun IngredientImage(
    name: String,
    modifier: Modifier = Modifier
) {
    val imageUrl = remember(name) {
        AppConstants.getIngredientImageUrl(name)
    }

    AsyncImage(
        model = imageUrl,
        contentDescription = name,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF7F2EE)),
        contentScale = ContentScale.Fit,
        error = painterResource(R.drawable.ic_dish_placeholder),
        placeholder = painterResource(R.drawable.ic_dish_placeholder)
    )
}
