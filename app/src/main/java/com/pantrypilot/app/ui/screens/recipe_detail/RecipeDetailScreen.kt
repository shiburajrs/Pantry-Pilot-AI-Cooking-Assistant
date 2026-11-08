package com.pantrypilot.app.ui.screens.recipe_detail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pantrypilot.app.R
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.data.model.RecipeIngredient
import com.pantrypilot.app.ui.components.PantryPilotAppBar
import com.pantrypilot.app.ui.components.PantryPilotPrimaryButton
import com.pantrypilot.app.ui.screens.recipe_discovery.RecipeDiscoveryViewModel
import com.pantrypilot.app.ui.screens.review_ingredients.IngredientImage
import com.pantrypilot.app.ui.theme.PantryFavorite

@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    onStartCookingClick: (recipe: Recipe) -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: RecipeDiscoveryViewModel = hiltViewModel()
) {
    val state by viewModel.coverImageState.collectAsStateWithLifecycle()
    val savedRecipeIds by viewModel.savedRecipeIds.collectAsStateWithLifecycle()
    val isSaved = savedRecipeIds.contains(recipe.id)

    LaunchedEffect(recipe.id) {
        viewModel.generateRecipeCover(recipe)
    }

    Scaffold(
        topBar = {
            PantryPilotAppBar(
                title = stringResource(R.string.recipe_details_title),
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleSaveRecipe(recipe) }
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isSaved) stringResource(R.string.cd_remove_from_saved) else stringResource(R.string.cd_save_recipe),
                            tint = if (isSaved) PantryFavorite else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 12.dp
                        ),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PantryPilotPrimaryButton(
                        text = stringResource(R.string.action_start_cooking),
                        onClick = { onStartCookingClick(recipe) },
                        enabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .padding(bottom = 12.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            RecipeCover(
                state = state,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurface
            )

            RecipeStatsRow(recipe)

            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.recipe_ingredients_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            recipe.usedIngredients.forEach { ingredient ->
                RecipeIngredientChip(ingredient)
            }

            recipe.missingIngredients.forEach { ingredient ->
                RecipeIngredientChip(ingredient)
            }
        }
    }
}

@Composable
fun RecipeStatsRow(recipe: Recipe) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(50.dp)
    ) {
        RecipeStatItem(
            icon = Icons.Outlined.AccessTime,
            value = stringResource(R.string.recipe_time_min_format, recipe.cookingTime)
        )

        RecipeStatItem(
            icon = Icons.Outlined.BarChart,
            value = recipe.difficulty
        )

        RecipeStatItem(
            icon = Icons.Outlined.People,
            value = stringResource(R.string.stat_servings) + " : ${recipe.servings}"
        )
    }
}

@Composable
fun RecipeStatItem(
    icon: ImageVector,
    value: String
) {
    Row(
        modifier = Modifier.padding(
            horizontal = 0.dp,
        ),
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
fun RecipeIngredientChip(ingredient: RecipeIngredient) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IngredientImage(
            ingredient.name,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            ingredient.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 2.sp,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            ingredient.quantity,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 2.sp,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RecipeCover(
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
                    message = state.message.ifBlank { stringResource(R.string.recipe_image_unavailable) }
                )
            }
        }
    }
}

@Composable
fun RecipePlaceholder(
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(
                R.drawable.ic_dish_placeholder
            ),
            contentDescription = stringResource(R.string.cd_dish_placeholder),
            modifier = Modifier.size(90.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
