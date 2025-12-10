package com.bagi_bill.bagi_bill.ui.screens.rincian

// FIXED: Import spesifik untuk ArrowBack (AutoMirrored)
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bagi_bill.composeapp.generated.resources.Res
import bagi_bill.composeapp.generated.resources.header_rincian
import com.bagi_bill.bagi_bill.ui.components.TranslucentIconButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RincianScreen(modifier: Modifier = Modifier) {
    // 1. BOX UTAMA: Wadah tumpukan
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 2. HEADER GAMBAR (Layer Belakang)
        Image(
            painter = painterResource(Res.drawable.header_rincian),
            contentDescription = "Header",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.TopCenter)
                // FIXED: Panggil fungsi yang sudah direname jadi huruf kecil
                .clip(bottomArcShape(curveMagnitude = 40.dp))
        )

        // LAYER 3: SCAFFOLD (Layer Depan)
        // FIXED: Scaffold ini HARUS berada di DALAM kurung kurawal Box agar menumpuk!
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    // FIXED: Title wajib diisi meski kosong
                    title = {Text("Rincian")
                            },
                    navigationIcon = {
                        TranslucentIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = {},
                            contentDescription = "Back",
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    // Tambahkan warna transparan agar TopBar tidak putih polos
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Konfirmasi")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .fillMaxSize()
            ) {
                Text("Area konten scrollable akan disini")
            }
        }
    }
}

// FIXED: Nama fungsi diawali huruf kecil (camelCase) karena mengembalikan nilai (Shape)
@Composable
fun bottomArcShape(curveMagnitude: Dp = 40.dp): Shape {
    val density = LocalDensity.current
    return remember(curveMagnitude) {
        androidx.compose.foundation.shape.GenericShape { size, _ ->
            val curveHeightPx = with(density) { curveMagnitude.toPx() }

            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - curveHeightPx)

            // FIXED: Ganti quadraticBezierTo menjadi quadraticTo
            quadraticTo(
                size.width / 2, size.height,
                0f, size.height - curveHeightPx
            )
            close()
        }
    }
}