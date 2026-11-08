package com.pantrypilot.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

object NavigationTransitions {
    const val ANIMATION_DURATION_ENTER = 350
    const val ANIMATION_DURATION_EXIT = 300

    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(ANIMATION_DURATION_ENTER, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(ANIMATION_DURATION_ENTER, easing = FastOutSlowInEasing)
        )
    }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            targetOffset = { fullWidth -> fullWidth / 4 },
            animationSpec = tween(ANIMATION_DURATION_EXIT, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(ANIMATION_DURATION_EXIT, easing = FastOutSlowInEasing)
        )
    }

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            initialOffset = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(ANIMATION_DURATION_ENTER, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(ANIMATION_DURATION_ENTER, easing = FastOutSlowInEasing)
        )
    }

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            animationSpec = tween(ANIMATION_DURATION_EXIT, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(ANIMATION_DURATION_EXIT, easing = FastOutSlowInEasing)
        )
    }
}
