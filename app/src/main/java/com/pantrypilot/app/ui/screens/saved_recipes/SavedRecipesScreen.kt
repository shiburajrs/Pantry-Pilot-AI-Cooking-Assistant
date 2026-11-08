package com.pantrypilot.app.ui.screens.saved_recipes

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.ui.components.PantryPilotAppBar
import com.pantrypilot.app.ui.components.PantryPilotPrimaryButton
import com.pantrypilot.app.ui.theme.PantryBackground
import com.pantrypilot.app.ui.theme.PantryFavorite
import com.pantrypilot.app.ui.theme.PantryOrange
import com.pantrypilot.app.ui.theme.PantryOrangeLight2
import com.pantrypilot.app.ui.theme.PantrySurface
import com.pantrypilot.app.ui.theme.PantryTextPrimary
import com.pantrypilot.app.ui.theme.PantryTextSecondary

@Composable
fun SavedRecipesScreen(
    viewModel: SavedRecipesViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onRecipeClick: (Recipe) -> Unit = {},
    onScanClick: () -> Unit = {}
) {
    val savedRecipes by viewModel.savedRecipes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PantryPilotAppBar(
                title = stringResource(R.string.saved_recipes_title),
                onBackClick = onBackClick
            )
        },
        containerColor = PantryBackground
    ) { innerPadding ->
        if (savedRecipes.isEmpty()) {
            EmptySavedRecipesView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                onScanClick = onScanClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.saved_recipes_count_format, savedRecipes.size),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PantryTextPrimary,
                        fontSize = 16.sp
                    )
                }

                items(
                    items = savedRecipes,
                    key = { it.id }
                ) { recipe ->
                    SavedRecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe) },
                        onRemoveClick = {
                            viewModel.removeRecipe(recipe.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedRecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PantrySurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_dish_placeholder),
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                        color = PantryTextPrimary,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.cd_remove_from_saved),
                            tint = PantryFavorite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = PantryTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PantryOrangeLight2
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = PantryOrange
                            )
                            Text(
                                text = stringResource(R.string.recipe_time_min_format, recipe.cookingTime),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = PantryOrange
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF0EAE4)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = PantryTextSecondary
                            )
                            Text(
                                text = stringResource(R.string.servings_short_format, recipe.servings),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = PantryTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptySavedRecipesView(
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(PantryOrangeLight2),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = PantryOrange,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.no_saved_recipes_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PantryTextPrimary,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.no_saved_recipes_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = PantryTextSecondary,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        PantryPilotPrimaryButton(
            text = stringResource(R.string.home_action_scan_title),
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}
