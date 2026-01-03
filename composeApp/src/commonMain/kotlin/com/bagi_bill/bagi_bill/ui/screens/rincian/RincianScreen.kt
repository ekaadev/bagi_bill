package com.bagi_bill.bagi_bill.ui.screens.rincian


import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import com.bagi_bill.bagi_bill.ParsedReceipt
import com.bagi_bill.bagi_bill.ui.components.BillItem
import com.bagi_bill.bagi_bill.ui.components.BillItemRow
import com.bagi_bill.bagi_bill.ui.components.BillSummaryRow
import com.bagi_bill.bagi_bill.ui.components.WhiteCircleIconButton
import com.bagi_bill.bagi_bill.utils.decodeByteArrayToImageBitmap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RincianScreen(
    parsedReceipt: ParsedReceipt,
    imageBytes: ByteArray? = null,
    onBack: () -> Unit,
    onRetakePhoto: () -> Unit = {},
    onEditDetails: () -> Unit = {},
    onConfirm: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Handle System Back Button
    BackHandler(onBack = onBack)

    // State untuk nama split bill
    var splitBillName by remember { mutableStateOf(parsedReceipt.name) }

    // State untuk info tambahan
    var additionalInfo by remember { mutableStateOf("") }

    // State untuk drawer
    val drawerState = rememberModalBottomSheetState()
    var showDrawer by remember { mutableStateOf(false) }

    // State sementara untuk input di drawer
    var tempAdditionalInfo by remember { mutableStateOf(additionalInfo) }

    // Konversi data dari ParsedReceipt ke BillItem untuk ditampilkan
    val billItems = remember(parsedReceipt) {
        parsedReceipt.items.map { receiptItem ->
            BillItem(
                name = receiptItem.name,
                qty = receiptItem.qty,
                price = receiptItem.price
            )
        }
    }

    // Konversi ByteArray ke ImageBitmap untuk ditampilkan
    val capturedImage: ImageBitmap? = remember(imageBytes) {
        imageBytes?.let {
            try {
                // Menggunakan platform-specific decoder
                decodeByteArrayToImageBitmap(it)
            } catch (e: Exception) {
                println("Error decoding image: ${e.message}")
                null
            }
        }
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
                // Gunakan shape lengkung di bagian bawah header
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
                                IconButton(onClick = onBack) {
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
                                    onClick = onBack,
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
                                value = splitBillName,
                                onValueChange = { splitBillName = it },
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
                                onClick = {
                                    tempAdditionalInfo = additionalInfo
                                    showDrawer = true
                                },
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
                                        text = if (additionalInfo.isEmpty()) {
                                            "Masukkin info tambahan di sini"
                                        } else {
                                            if (additionalInfo.length > 35) {
                                                additionalInfo.take(35) + ".."
                                            } else {
                                                additionalInfo
                                            }
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (additionalInfo.isEmpty()) Color.Gray else Color.Black,
                                        maxLines = 1
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
                                if (capturedImage != null) {
                                    Image(
                                        bitmap = capturedImage,
                                        contentDescription = "Foto Struk",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.LightGray)
                                    )
                                } else {
                                    Image(
                                        // Gambar Placeholder
                                        painter = painterResource(Res.drawable.header_rincian),
                                        contentDescription = "Foto Struk",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.LightGray)
                                    )
                                }

                               // Button Foto Ulang
                                OutlinedButton(
                                    onClick = onRetakePhoto,
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
                            // LIST BARANG (Dari hasil OCR)
                            billItems.forEach { item ->
                                BillItemRow(item = item)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // B. SEPARATOR (Garis Putus-Putus / Lurus)
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.LightGray.copy(alpha = 0.5f) // Abu tipis
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // SUMMARY SECTION (Dari hasil OCR)
                            BillSummaryRow("Subtotal", formatPrice(parsedReceipt.summary.subtotal))
                            BillSummaryRow("Pajak", formatPrice(parsedReceipt.summary.pajak))
                            BillSummaryRow("Servis", formatPrice(parsedReceipt.summary.servis))
                            BillSummaryRow("Diskon", formatPrice(parsedReceipt.summary.diskon))
                            BillSummaryRow("Lainnya", formatPrice(parsedReceipt.summary.lainnya))

                            Spacer(modifier = Modifier.height(8.dp))

                            // TOTAL (Bold)
                            BillSummaryRow("Jumlah total", formatPrice(parsedReceipt.summary.total), isTotal = true)

                            Spacer(modifier = Modifier.height(24.dp))

                            // TOMBOL UBAH RINCIAN
                            OutlinedButton(
                                onClick = onEditDetails,
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

        // Modal Bottom Sheet untuk Info Tambahan
        if (showDrawer) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDrawer = false
                    tempAdditionalInfo = additionalInfo // Reset ke nilai asli jika dibatalkan
                },
                sheetState = drawerState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp)
                ) {
                    // Header dengan tombol Close dan Hapus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol Close
                        IconButton(onClick = {
                            showDrawer = false
                            tempAdditionalInfo = additionalInfo // Reset
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = Color.Black
                            )
                        }

                        // Judul
                        Text(
                            text = "Catatan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        // Tombol Hapus
                        TextButton(
                            onClick = {
                                tempAdditionalInfo = ""
                            }
                        ) {
                            Text(
                                text = "Hapus",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field Input
                    Text(
                        text = "Masukkin info tambahan di sini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tempAdditionalInfo,
                        onValueChange = { tempAdditionalInfo = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = {
                            Text(
                                text = "Tulis catatan di sini...",
                                color = Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 8
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Counter karakter
                    Text(
                        text = "${tempAdditionalInfo.length}/160",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tombol Selesai
                    Button(
                        onClick = {
                            additionalInfo = tempAdditionalInfo
                            showDrawer = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Selesai",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Fungsi helper untuk format harga
private fun formatPrice(price: Int): String {
    // Secara eksplisit menangani nilai 0: tidak ada pemisah ribuan yang perlu diterapkan,
    // sehingga "0" ditampilkan apa adanya.
    if (price == 0) return "0"

    return price.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
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

// Preview untuk testing di IDE
@Preview
@Composable
private fun RincianScreenPreview() {
    val dummyReceipt = ParsedReceipt(
        name = "Toko ABC",
        items = listOf(
            com.bagi_bill.bagi_bill.ReceiptItem(1, "Spons Make Up", 14500),
            com.bagi_bill.bagi_bill.ReceiptItem(1, "Kacamata Gaya", 13500),
            com.bagi_bill.bagi_bill.ReceiptItem(1, "Anting Anting", 8000),
        ),
        summary = com.bagi_bill.bagi_bill.ReceiptSummary(
            subtotal = 36000,
            pajak = 0,
            diskon = 0,
            lainnya = 0,
            total = 36000
        )
    )

    RincianScreen(
        parsedReceipt = dummyReceipt,
        imageBytes = null,
        onBack = {},
        onRetakePhoto = {}
    )
}
