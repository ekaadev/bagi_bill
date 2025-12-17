package com.bagi_bill.bagi_bill.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

//Komponen Top Bar Button Bulat
@Composable
fun WhiteCircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    iconTint: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(40.dp),
        shape = CircleShape, // Bulat sempurna
        color = Color.White, // Background Putih
        shadowElevation = 2.dp // (Opsional) Kasih bayangan dikit biar pop-up
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp)

        ) {

            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint, // Warna Icon (Ungu)
                modifier = Modifier.size(24.dp)
            )
        }
    }
}