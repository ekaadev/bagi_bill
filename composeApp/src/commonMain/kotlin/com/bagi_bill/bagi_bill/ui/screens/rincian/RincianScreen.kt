package com.bagi_bill.bagi_bill.ui.screens.rincian


import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bagi_bill.composeapp.generated.resources.Res
import bagi_bill.composeapp.generated.resources.header_rincian
import com.bagi_bill.bagi_bill.ui.components.BillItem
import com.bagi_bill.bagi_bill.ui.components.BillItemRow
import com.bagi_bill.bagi_bill.ui.components.BillSummaryRow
import com.bagi_bill.bagi_bill.ui.components.WhiteCircleIconButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RincianScreen(modifier: Modifier = Modifier) {

    // Data Dummy Sementara
    val dummyItems = remember{
        listOf(
            BillItem("Spons Make Up", 1, 14500),
            BillItem("Kacamata Gaya", 1, 13500),
            BillItem("Anting Anting", 1, 8000),
            BillItem("Ikat Rambut", 10, 4000),
            BillItem("Alat Pencabut Alis", 1, 10000),
            BillItem("Anting Anting Anting", 2, 8000),
        )
    }


    val listState =rememberLazyListState()

    val isScrolled by remember { derivedStateOf { listState.firstVisibleItemScrollOffset > 50 || listState.firstVisibleItemIndex > 0 } }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // HEADER GAMBAR (Layer Belakang)
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
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // CROSSFADE: Transisi mulus antara dua layout berbeda
                Crossfade(
                    targetState = isScrolled,
                    animationSpec = tween(durationMillis = 500),
                    label = "TopBarAnimation"
                ) { scrolled ->

                    if (scrolled) {
                        // ============================================================
                        // KONDISI 1: SUDAH SCROLL (Background Putih, Icon Hitam)
                        // ============================================================
                        TopAppBar(
                            windowInsets = WindowInsets.statusBars,
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White // Putih Solid
                            ),
                            navigationIcon = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.Black
                                    )
                                }
                            },
                            title = {
                                Text(
                                    "Rincian split bill",
                                    style = TextStyle(
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp
                                    ),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            },
                            actions = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Help,
                                        contentDescription = "Bantuan",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        )
                    } else {
                        // ============================================================
                        // KONDISI 2: BELUM SCROLL (Transparan, Icon Bulat)
                        // ============================================================
                        TopAppBar(
                            windowInsets = WindowInsets.statusBars,
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent // Bening Total
                            ),
                            navigationIcon = {
                                WhiteCircleIconButton(
                                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                                    onClick = {},
                                    contentDescription = "Back",
                                    modifier = Modifier.padding(start = 16.dp),
                                    iconTint = Color.Black
                                )
                            },
                            title = {},
                            actions = {
                                WhiteCircleIconButton(
                                    icon = Icons.AutoMirrored.Filled.Help,
                                    onClick = {},
                                    contentDescription = "Bantuan",
                                    modifier = Modifier.padding(end = 16.dp),
                                    iconTint = Color.Gray
                                )
                            }
                        )
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 32.dp,
                            clip = false,
                            spotColor = Color.Black,
                            ambientColor = Color.Black,
                        ),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),


                ){
                    Column (
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp,
                            bottom = 24.dp,
                        ),
                    ){
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth().height(43.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        ){
                            Text(
                                text = "Konfirmasi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                //Spacer untuk Konten Menutupi Gambar Header
                item {
                    Spacer(modifier = Modifier.height(140.dp))
                }

                //Nama Split Bill
                item {
                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-30).dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ){
                        Column(
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
                        ){
                            Text(
                                "Nama Split Bill",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray,
                            )
                            TextField(
                                value = "",
                                onValueChange = {},
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                ),
                                placeholder = {
                                    Text("Kasih nama split bill disini",
                                        modifier = Modifier.padding(all = 0.dp)

                                )},
                                modifier = Modifier.fillMaxWidth(),

                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color(0xFF5E35B1),
                                    unfocusedIndicatorColor = Color.LightGray
                                )
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Surface(
                                onClick = { /* Todo: Buka dialog info */ },
                                color = Color(0xFFF5F5F5),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Masukkin info tambahan di sini",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp) ,
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            //Header
                            Text(
                                text = "Struk berhasil di-scan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Klik gambar di bawah buat liat foto struk lebih jelas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Gambar dan Tombol
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Hasil Scan
                                Image(
                                   // Gambar Sementara
                                    painter = painterResource(Res.drawable.header_rincian),
                                    contentDescription = "Foto Struk",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.LightGray)
                                )

                               // Button Foto Ulang
                                OutlinedButton(
                                    onClick = { /* Aksi Foto Ulang */ },
                                    shape = RoundedCornerShape(50),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary,
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Foto ulang",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp, bottom = 32.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // LIST BARANG (Looping)
                            dummyItems.forEach { item ->
                                BillItemRow(item = item)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // B. SEPARATOR (Garis Putus-Putus / Lurus)
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.LightGray.copy(alpha = 0.5f) // Abu tipis
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // SUMMARY SECTION
                            BillSummaryRow("Subtotal", "50.000")
                            BillSummaryRow("Pajak", "0")
                            BillSummaryRow("Servis", "0")
                            BillSummaryRow("Diskon", "0")
                            BillSummaryRow("Lainnya", "0")

                            Spacer(modifier = Modifier.height(8.dp))

                            // TOTAL (Bold)
                            BillSummaryRow("Jumlah total", "50.000", isTotal = true)

                            Spacer(modifier = Modifier.height(24.dp))

                            // TOMBOL UBAH RINCIAN
                            OutlinedButton(
                                onClick = { /* Todo: Mode Edit */ },
                                modifier = Modifier.fillMaxWidth().height(45.dp),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.DarkGray
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ubah rincian", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

//Fungsi untuk Membuat Lengkungan pada Gambar Header
@Composable
fun bottomArcShape(curveMagnitude: Dp = 40.dp): Shape {
    val density = LocalDensity.current
    return remember(curveMagnitude) {
        androidx.compose.foundation.shape.GenericShape { size, _ ->
            val curveHeightPx = with(density) { curveMagnitude.toPx() }

            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - curveHeightPx)

            quadraticTo(
                size.width / 2, size.height,
                0f, size.height - curveHeightPx
            )
            close()
        }
    }
}