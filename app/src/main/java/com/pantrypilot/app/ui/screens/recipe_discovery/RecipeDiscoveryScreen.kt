package com.pantrypilot.app.ui.screens.recipe_discovery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.pantrypilot.app.data.model.Ingredient
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.data.model.RecipeIngredient
import com.pantrypilot.app.ui.components.CircularLoadingIndicator
import com.pantrypilot.app.ui.components.PantryPilotAppBar
import com.pantrypilot.app.ui.theme.PantryBackground
import com.pantrypilot.app.ui.theme.PantryFavorite
import com.pantrypilot.app.ui.theme.PantryOrange
import com.pantrypilot.app.ui.theme.PantryOrangeLight2
import com.pantrypilot.app.ui.theme.PantrySuccessLight
import com.pantrypilot.app.ui.theme.PantryTextPrimary
import com.pantrypilot.app.ui.theme.PantryTextSecondary

@Composable
fun RecipeDiscoveryScreen(
    ingredients: List<Ingredient>,
    viewModel: RecipeDiscoveryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onRecipeDetail: (Recipe) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.findRecipes(ingredients)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val savedRecipeIds by viewModel.savedRecipeIds.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PantryPilotAppBar(
                title = stringResource(R.string.recipe_suggestions_title),
                onBackClick = onBackClick
            )
        },
        containerColor = PantryBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 15.dp)
        ) {
            when (val result = state) {
                RecipeUiState.Idle -> {}

                RecipeUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularLoadingIndicator()
                    }
                }

                is RecipeUiState.Success -> {
                    if (result.recipes.isEmpty()) {
                        EmptyRecipesView(
                            onAdjustIngredientsClick = onBackClick
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Text(
                                    text = stringResource(R.string.recipe_suggestions_count, result.recipes.size),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 15.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                            items(
                                items = result.recipes,
                                key = { it.id }
                            ) { recipe ->
                                val isSaved = savedRecipeIds.contains(recipe.id)
                                RecipeCard(
                                    recipe = recipe,
                                    isSaved = isSaved,
                                    onToggleSave = { viewModel.toggleSaveRecipe(recipe) },
                                    onClick = { onRecipeDetail(recipe) }
                                )
                            }
                        }
                    }
                }

                is RecipeUiState.Error -> {
                    RecipeDiscoveryErrorView(
                        message = result.message,
                        onRetryClick = {
                            viewModel.findRecipes(ingredients, forceRefresh = true)
                        },
                        onAdjustIngredientsClick = onBackClick
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeDiscoveryErrorView(
    message: String,
    onRetryClick: () -> Unit,
    onAdjustIngredientsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFFDE8E8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.error_could_not_find_recipes),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PantryTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = PantryTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetryClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PantryOrange
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.action_try_again), fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onAdjustIngredientsClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.action_adjust_ingredients), color = PantryTextPrimary)
        }
    }
}

@Composable
fun EmptyRecipesView(
    onAdjustIngredientsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(PantryOrangeLight2),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.RestaurantMenu,
                contentDescription = null,
                tint = PantryOrange,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.no_recipes_found_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PantryTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.no_recipes_found_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = PantryTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAdjustIngredientsClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PantryOrange
            )
        ) {
            Text(stringResource(R.string.action_add_more_ingredients), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: (Recipe) -> Unit
) {
    Card(
        onClick = { onClick(recipe) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_dish_placeholder),
                contentDescription = stringResource(R.string.cd_recipe_image),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )

                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isSaved) stringResource(R.string.cd_remove_from_saved) else stringResource(R.string.cd_save_recipe),
                            tint = if (isSaved) PantryFavorite else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RecipeInfoChip(
                        time = stringResource(R.string.recipe_time_min_format, recipe.cookingTime)
                    )
                }

                IngredientAvailabilityStatus(recipe.missingIngredients)
            }
        }
    }
}

@Composable
fun RecipeInfoChip(
    time: String
) {
    Row(
        modifier = Modifier.padding(
            horizontal = 10.dp,
            vertical = 6.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = stringResource(R.string.cd_cooking_time),
            modifier = Modifier.size(15.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun IngredientAvailabilityStatus(missingIngredients: List<RecipeIngredient>) {
    val hasMissingIngredients = missingIngredients.isNotEmpty()

    Surface(
        color = if (hasMissingIngredients) PantryOrangeLight2 else PantrySuccessLight,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = if (hasMissingIngredients)
                stringResource(R.string.missing_ingredients_count_format, missingIngredients.size)
            else
                stringResource(R.string.all_ingredients_available),
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
