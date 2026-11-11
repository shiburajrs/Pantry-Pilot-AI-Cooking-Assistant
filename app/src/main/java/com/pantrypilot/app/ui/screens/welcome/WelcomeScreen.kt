package com.pantrypilot.app.ui.screens.welcome

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pantrypilot.app.R
import com.pantrypilot.app.ui.theme.PantryBackground
import com.pantrypilot.app.ui.theme.PantryOrange
import com.pantrypilot.app.ui.theme.PantryOrangeDark
import com.pantrypilot.app.ui.theme.PantryOrangeLight2
import com.pantrypilot.app.ui.theme.PantryOutline
import com.pantrypilot.app.ui.theme.PantrySurface
import com.pantrypilot.app.ui.theme.PantryTextPrimary
import com.pantrypilot.app.ui.theme.PantryTextSecondary
import kotlinx.coroutines.launch

private data class WelcomePageItem(
    val imageRes: Int,
    val titleRes: Int,
    val descriptionRes: Int
)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    val pages = listOf(
        WelcomePageItem(
            imageRes = R.drawable.img_welcome_ingredients,
            titleRes = R.string.welcome_feature_1_title,
            descriptionRes = R.string.welcome_feature_1_desc
        ),
        WelcomePageItem(
            imageRes = R.drawable.img_welcome_ai_recipes,
            titleRes = R.string.welcome_feature_2_title,
            descriptionRes = R.string.welcome_feature_2_desc
        ),
        WelcomePageItem(
            imageRes = R.drawable.img_welcome_dish,
            titleRes = R.string.welcome_feature_3_title,
            descriptionRes = R.string.welcome_feature_3_desc
        )
    )

    val pageCount = pages.size
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pageCount - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PantryBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // Top Header: Page Counter Pill and Skip Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PantryOrangeLight2,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.welcome_page_counter,
                        pagerState.currentPage + 1,
                        pageCount
                    ),
                    color = PantryOrangeDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            if (!isLastPage) {
                TextButton(
                    onClick = onGetStarted
                ) {
                    Text(
                        text = stringResource(R.string.action_skip),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PantryTextSecondary
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // Horizontal Pager with 3 screens
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = pages[pageIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Card container for illustration
                Box(
                    modifier = Modifier
                        .size(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(page.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(page.titleRes),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PantryTextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(page.descriptionRes),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = PantryTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Bottom Controls: Page Indicator & Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicator Dots
            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index ->
                    val isSelected = pagerState.currentPage == index
                    val animatedWidth by animateDpAsState(
                        targetValue = if (isSelected) 26.dp else 8.dp,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                        label = "indicator_width"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(animatedWidth)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) PantryOrange else PantryOrange.copy(alpha = 0.25f)
                            )
                    )
                }
            }

            // Primary Action Button (Next or Get Started)
            Button(
                onClick = {
                    if (isLastPage) {
                        onGetStarted()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PantryOrange,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isLastPage) {
                        stringResource(R.string.action_get_started)
                    } else {
                        stringResource(R.string.action_next)
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Skip Button (or placeholder on last page)
            if (!isLastPage) {
                TextButton(
                    onClick = onGetStarted
                ) {
                    Text(
                        text = stringResource(R.string.action_skip),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = PantryTextSecondary
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
