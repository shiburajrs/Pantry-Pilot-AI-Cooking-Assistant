package com.pantrypilot.app.ui.screens.home

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pantrypilot.app.R

@Composable
fun HomeScreen(
    onScanIngredients: () -> Unit = {},
    onAddManually: () -> Unit = {},
    onSavedRecipes: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFFFF8F1)
    val primaryColor = Color(0xFFD94F16)
    val textColor = Color(0xFF32180F)
    val secondaryText = Color(0xFF806D63)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(horizontal = 18.dp)
            .padding(top = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.img_app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.app_name_display),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(38.dp))

        Text(
            text = stringResource(R.string.home_headline),
            fontSize = 27.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.home_subheadline),
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = secondaryText
        )

        Spacer(modifier = Modifier.height(30.dp))

        HomeActionCard(
            title = stringResource(R.string.home_action_scan_title),
            subtitle = stringResource(R.string.home_action_scan_subtitle),
            icon = Icons.Default.CameraAlt,
            isPrimary = true,
            onClick = onScanIngredients
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionCard(
            title = stringResource(R.string.home_action_manual_title),
            subtitle = stringResource(R.string.home_action_manual_subtitle),
            icon = Icons.Default.Edit,
            isPrimary = false,
            onClick = onAddManually
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionCard(
            title = stringResource(R.string.home_action_saved_title),
            subtitle = stringResource(R.string.home_action_saved_subtitle),
            icon = Icons.Default.FavoriteBorder,
            isPrimary = false,
            onClick = onSavedRecipes
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun HomeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFFD94F16)
    val textColor = Color(0xFF32180F)

    val containerColor = if (isPrimary) {
        primaryColor
    } else {
        Color(0xFFF5EAE1)
    }

    val contentColor = if (isPrimary) {
        Color.White
    } else {
        textColor
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPrimary) {
                            Color.White.copy(alpha = 0.18f)
                        } else {
                            Color(0xFFFFF8F1)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPrimary) {
                        Color.White
                    } else {
                        primaryColor
                    },
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}
