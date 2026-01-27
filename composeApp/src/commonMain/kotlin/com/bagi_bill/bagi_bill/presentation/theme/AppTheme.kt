package com.bagi_bill.bagi_bill.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * App-wide UI theme constants extracted from HomeScreen and HistoryScreen patterns.
 * Use these constants for consistent styling across all screens.
 */
object AppTheme {
    
    // ==================== SPACING ====================
    object Spacing {
        val xxSmall = 2.dp
        val xSmall = 4.dp
        val small = 8.dp
        val medium = 12.dp
        val large = 16.dp
        val xLarge = 24.dp
        val xxLarge = 32.dp
    }
    
    // ==================== CORNER RADIUS ====================
    object Radius {
        val card = 16.dp           // Card sections
        val bottomSheet = 32.dp    // Bottom sheet top corners
        val pill = 50.dp           // Pills, buttons, chips
        val iconCircle = 35.dp     // Icon background circles
        val button = 25.dp         // Standard buttons
    }
    
    // ==================== SHAPES ====================
    object Shapes {
        val card = RoundedCornerShape(Radius.card)
        val pill = RoundedCornerShape(Radius.pill)
        val iconCircle = RoundedCornerShape(Radius.iconCircle)
        val button = RoundedCornerShape(Radius.button)
        val bottomSheet = RoundedCornerShape(topStart = Radius.bottomSheet, topEnd = Radius.bottomSheet)
    }
    
    // ==================== SIZES ====================
    object Size {
        val iconSmall = 16.dp
        val iconMedium = 24.dp
        val iconLarge = 40.dp
        val buttonHeight = 43.dp
        val buttonHeightLarge = 50.dp
        val shutterButton = 78.dp
        val controlButton = 48.dp
    }
    
    // ==================== COLORS ====================
    object Colors {
        val cardBackground = Color.White
        val borderLight = Color.Gray.copy(alpha = 0.3f)
        val divider = Color.Gray.copy(alpha = 0.1f)
        val overlayDark = Color.Gray.copy(alpha = 0.6f)
        val textLight = Color.LightGray
    }
    
    // ==================== ELEVATION ====================
    object Elevation {
        val none = 0.dp
        val bottomSticky = 32.dp
    }
    
    // ==================== PADDING PRESETS ====================
    object Padding {
        // Card outer padding
        val cardOuter = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        // Card content padding
        val cardContent = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        // Row item padding
        val rowItem = 16.dp
        // Bottom sticky padding
        val bottomSticky = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp)
    }
}

/**
 * Simple data class for padding values
 */
data class PaddingValues(
    val horizontal: androidx.compose.ui.unit.Dp = 0.dp,
    val vertical: androidx.compose.ui.unit.Dp = 0.dp,
    val start: androidx.compose.ui.unit.Dp = horizontal,
    val end: androidx.compose.ui.unit.Dp = horizontal,
    val top: androidx.compose.ui.unit.Dp = vertical,
    val bottom: androidx.compose.ui.unit.Dp = vertical
)
