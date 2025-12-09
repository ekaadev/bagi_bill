package com.bagi_bill.bagi_bill

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreviewScreen(
    photoBytes: ByteArray,
    isFromGallery: Boolean, // <--- PARAMETER BARU
    onRetake: () -> Unit,
    onConfirm: () -> Unit
) {
    val imageBitmap = rememberBitmapFromBytes(photoBytes)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Tampilkan gambar
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Preview Foto",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "Gagal memuat gambar",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Tombol Back (Kiri Atas) - Tetap ada agar user bisa batal
        IconButton(
            onClick = onRetake,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Tombol Aksi (Bawah)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LOGIKA: Jika BUKAN dari galeri (alias dari kamera), tampilkan tombol "Foto Ulang"
            if (!isFromGallery) {
                Button(
                    onClick = onRetake,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Foto ulang",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Tombol Pakai Foto (Selalu Ada)
            // Kalau dari galeri, dia akan otomatis melebar memenuhi baris (karena weight 1f)
            // yang membuatnya terlihat rapi di tengah.
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Pakai foto ini",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}