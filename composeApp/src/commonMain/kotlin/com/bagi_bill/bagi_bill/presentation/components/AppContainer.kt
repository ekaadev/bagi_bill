package com.bagi_bill.bagi_bill.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * AppContainer - Adaptive Layout Container
 *
 * Untuk Mobile (< 600dp): Render konten fullscreen
 * Untuk Web/Desktop (>= 600dp): Render konten dengan horizontal padding seperti modern web
 */
@Composable
fun AppContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isMobile = maxWidth < 600.dp

        if (isMobile) {
            // Mobile: Full screen content
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                content()
            }
        } else {
            // Web/Desktop: Modern web layout dengan horizontal padding yang proporsional
            // Seperti website modern yang tidak terlalu kecil di tengah

            // Calculate max content width dan horizontal padding berdasarkan screen size
            val (maxContentWidth, horizontalPadding) = when {
                maxWidth >= 1400.dp -> Pair(900.dp, 120.dp)   // Very large desktop
                maxWidth >= 1200.dp -> Pair(800.dp, 80.dp)    // Large desktop
                maxWidth >= 900.dp -> Pair(700.dp, 60.dp)     // Medium desktop
                maxWidth >= 600.dp -> Pair(560.dp, 40.dp)     // Small desktop/tablet
                else -> Pair(maxWidth, 0.dp)
            }

            // Subtle background untuk web
            val backgroundColor = Color(0xFFF8F9FC)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = maxContentWidth)
                        .fillMaxHeight()
                        .padding(horizontal = horizontalPadding)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * AppContainer dengan Scaffold built-in untuk kemudahan penggunaan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffoldContainer(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    AppContainer(
        modifier = modifier
    ) {
        Scaffold(
            topBar = topBar,
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            snackbarHost = snackbarHost,
            content = content
        )
    }
}

