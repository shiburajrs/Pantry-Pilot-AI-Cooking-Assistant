package com.pantrypilot.app.ui.screens.cooking_steps

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.pantrypilot.app.R
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.ui.components.PantryPilotAppBar
import com.pantrypilot.app.ui.components.PantryPilotPrimaryButton
import com.pantrypilot.app.ui.screens.recipe_detail.RecipeCoverUiState
import com.pantrypilot.app.ui.screens.recipe_detail.RecipePlaceholder
import com.pantrypilot.app.ui.screens.recipe_discovery.RecipeDiscoveryViewModel
import com.pantrypilot.app.ui.theme.PantryOrange
import com.pantrypilot.app.ui.theme.PantryOrangeLight2
import com.pantrypilot.app.ui.theme.PantrySurface
import com.pantrypilot.app.ui.theme.PantryTextSecondary

@Composable
fun CookingStepsScreen(
    recipe: Recipe,
    onBackClick: () -> Unit,
    onFinishCooking: () -> Unit,
    viewModel: RecipeDiscoveryViewModel = hiltViewModel()
) {
    val steps = recipe.instructions
    val totalSteps = steps.size

    var currentStep by rememberSaveable(recipe.id) {
        mutableIntStateOf(0)
    }
    val state by viewModel.coverImageState.collectAsStateWithLifecycle()
    val savedRecipeIds by viewModel.savedRecipeIds.collectAsStateWithLifecycle()
    val isSaved = savedRecipeIds.contains(recipe.id)
    var showCompletionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.generateRecipeStepCover(recipe, currentStep)
    }

    val isLastStep = currentStep == totalSteps - 1

    Scaffold(
        topBar = {
            Column {
                PantryPilotAppBar(
                    title = stringResource(R.string.cooking_steps_title),
                    onBackClick = onBackClick
                )

                if (totalSteps > 0) {
                    CookingStepProgress(
                        currentStep = currentStep,
                        totalSteps = totalSteps
                    )
                }
            }
        },

        bottomBar = {
            if (totalSteps > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            IconButton(
                                onClick = {
                                    if (currentStep > 0) {
                                        currentStep--
                                        viewModel.generateRecipeStepCover(recipe, currentStep)
                                    } else {
                                        onBackClick()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_previous_step)
                                )
                            }
                        }

                        PantryPilotPrimaryButton(
                            onClick = {
                                if (isLastStep) {
                                    if (!isSaved) {
                                        showCompletionDialog = true
                                    } else {
                                        onFinishCooking()
                                    }
                                } else {
                                    currentStep++
                                    viewModel.generateRecipeStepCover(recipe, currentStep)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            text = if (isLastStep) {
                                stringResource(R.string.action_finish_cooking)
                            } else {
                                stringResource(R.string.action_next_step)
                            },
                            enabled = true
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (totalSteps == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_cooking_instructions))
            }
        } else {
            val instruction = steps[currentStep]

            val stepDescription = if (instruction.contains(".")) {
                instruction.substringAfter(".").trim()
                    .ifBlank { instruction }
            } else {
                instruction
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RecipeStepCover(state)

                Text(
                    text = stepDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PantryOrangeLight2),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = null,
                        tint = PantryOrange,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.cooking_completed_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.cooking_completed_desc, recipe.name),
                    style = MaterialTheme.typography.bodyMedium,
                    color = PantryTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleSaveRecipe(recipe)
                        showCompletionDialog = false
                        onFinishCooking()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PantryOrange)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.action_save_and_go_home),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCompletionDialog = false
                        onFinishCooking()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.action_back_to_home),
                        color = PantryTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = PantrySurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun CookingStepProgress(
    currentStep: Int,
    totalSteps: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.step_progress_format, currentStep + 1, totalSteps),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (index <= currentStep) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun RecipeStepCover(
    state: RecipeCoverUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F1F1)),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            RecipeCoverUiState.Idle -> {
                RecipePlaceholder(
                    message = stringResource(R.string.recipe_image_label)
                )
            }

            RecipeCoverUiState.Loading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(
                            R.drawable.ic_dish_placeholder
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(85.dp)
                    )

                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(R.string.generating_recipe_image),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            is RecipeCoverUiState.Success -> {
                Image(
                    bitmap = state.coverImage.asImageBitmap(),
                    contentDescription = stringResource(R.string.cd_recipe_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            is RecipeCoverUiState.Error -> {
                RecipePlaceholder(
                    message = state.message.ifBlank { stringResource(R.string.step_visual_unavailable) }
                )
            }
        }
    }
}
