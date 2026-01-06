package com.bagi_bill.bagi_bill

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.rememberGraphicsLayer
import bagi_bill.composeapp.generated.resources.Res
import bagi_bill.composeapp.generated.resources.logo_bca
import com.bagi_bill.bagi_bill.utils.decodeByteArrayToImageBitmap
import com.bagi_bill.bagi_bill.utils.rememberShareHelper
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import bagi_bill.composeapp.generated.resources.header_rincian
import com.bagi_bill.bagi_bill.model.AssignableBillItem
import com.bagi_bill.bagi_bill.model.Member
import com.bagi_bill.bagi_bill.model.SplitBillData
import com.bagi_bill.bagi_bill.model.ProcessedItem
import com.bagi_bill.bagi_bill.model.ProcessedMember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneScreen(
    splitBillData: SplitBillData,
    assignedItems: List<AssignableBillItem>,
    imageBytes: ByteArray? = null,
    transactionDate: String? = null,
    onNavigateToRincian: () -> Unit = {},
    onBack: () -> Unit = {},
    onGoHome: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var showImageDialog by remember { mutableStateOf(false) }

    val shareHelper = rememberShareHelper()
    val graphicsLayer = rememberGraphicsLayer()

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

    // State untuk kontrol capture offscreen
    var triggerCapture by remember { mutableStateOf(false) }

    // --- UTILS ---
    fun formatCurrency(amount: Int): String {
        val reversed = amount.toString().reversed()
        val chunked = reversed.chunked(3).joinToString(".")
        return "Rp${chunked.reversed()}"
    }

    // --- DATA PROCESSING ---
    val processedMembers = remember(splitBillData, assignedItems) {
        val allMembers = listOf(splitBillData.payer) + splitBillData.members

        allMembers.map { member ->
            val myItems = assignedItems.filter { it.assignedMemberIds.contains(member.id) }
                .map { item ->
                    val splitCount = item.assignedMemberIds.size.coerceAtLeast(1)
                    val sharePrice = item.price / splitCount
                    ProcessedItem(
                        name = item.name,
                        qty = item.qty,
                        sharePrice = sharePrice
                    )
                }

            val totalPay = myItems.sumOf { it.sharePrice }

            ProcessedMember(
                member = member,
                isPayer = member.id == splitBillData.payer.id,
                items = myItems,
                totalToPay = totalPay
            )
        }
    }

    val totalBillAmount = remember(assignedItems) {
        assignedItems.sumOf { it.price }
    }

    if (showImageDialog && capturedImage != null) {
        Dialog(
            onDismissRequest = { showImageDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false // Agar bisa full width
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showImageDialog = false } // Klik background tutup
                    .background(Color.Black.copy(alpha = 0.8f)), // Gelap transparan
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = capturedImage,
                    contentDescription = "Full Image",
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // 90% layar
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = false) {} // Disable click on image (supaya gak close kalau klik gambar)
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = Color.White // Putih saat scroll
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        // Back ke Home Screen (sesuai request)
                        onGoHome() 
                    }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(6.dp),
                            tint = Color.Black
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            text = "Rincian split bill",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp,
                            color = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToRincian() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(6.dp),
                            tint = Color.Gray,
                        )
                    }

                    IconButton(onClick = {
                        triggerCapture = true // Pemicu capture layout khusus
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(6.dp),
                            tint = Color.Gray,
                        )
                    }

                    IconButton(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Fungsi Bantuan belum tersedia")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "Bantuan",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(6.dp),
                            tint = Color.Gray,
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        
        Box(modifier = Modifier.fillMaxSize()) {
            
            // LAYOUT KHUSUS CAPTURE (Offscreen / Hidden)
            // Ditaruh di dalam Box tapi dengan Alpha 0 agar tidak terlihat user (tapi tetap di-render)
            // Menggunakan verticalScroll agar konten bisa diukur melebihi tinggi layar (tidak terpotong)
            if (triggerCapture) {
                 Box(
                     modifier = Modifier
                         .alpha(0f)
                         .verticalScroll(rememberScrollState())
                 ) {
                     CaptureLayout(
                         splitBillData = splitBillData,
                         processedMembers = processedMembers,
                         totalBillAmount = totalBillAmount,
                         transactionDate = transactionDate,
                         capturedImage = capturedImage,
                         formatCurrency = ::formatCurrency,
                         graphicsLayer = graphicsLayer,
                         onCaptured = { bitmap ->
                             triggerCapture = false // Reset trigger
                             scope.launch {
                                 try {
                                     shareHelper.shareBillImage(bitmap)
                                 } catch (e: Exception) {
                                     snackbarHostState.showSnackbar("Gagal membagikan gambar: ${e.message}")
                                 }
                             }
                         }
                     )
                 }
            }

            // KOTAK PEMBUNGKUS UTAMA (LAYAR)
            Column(
                modifier = Modifier
                    .padding(paddingValues) // PENTING: Turunkan konten di bawah TopBar
                    .fillMaxSize()
                    .padding(horizontal = 16.dp) // Jarak dari tepi layar HP
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            Spacer(modifier = Modifier.height(16.dp))

            // KOTAK KARTU (STRUK/BILL)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawContent()
                    }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Padding di DALAM kartu (jarak konten ke tepi biru)
                    horizontalAlignment = Alignment.CenterHorizontally // Agar semua anak di tengah horizontal
                ) {

                // BAGIAN 1: INFO ATAS (Image, Toko, Tanggal)
                // Tidak perlu Row satu-satu, cukup Column ini sudah center semua

                // Gambar
                if (capturedImage != null) {
                    Image(
                        bitmap = capturedImage,
                        contentDescription = "Gambar struk",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .clickable { showImageDialog = true }
                    )
                }
                 else {
                     Image(
                         painter = painterResource(Res.drawable.header_rincian),
                         contentDescription = "Gambar struk",
                         contentScale = ContentScale.Crop,
                         modifier = Modifier
                             .size(80.dp)
                             .clip(RoundedCornerShape(10.dp))
                             .background(Color.White) // Opsional: biar gambar menonjol
                     )
                 }

                Spacer(modifier = Modifier.height(8.dp)) // Jarak antar elemen

                // Nama Toko
                Text(
                    text = splitBillData.merchantName,
                    color = Color.Black, // Warna teks kontras
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                 // Tanggal
                Text(
                    text = transactionDate ?: "Tanggal tidak tersedia",
                    color = Color.Black.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp)) // Jarak sebelum garis

                // GARIS PUTUS-PUTUS 1
                DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))

                // BAGIAN 2: HARGA
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Biaya",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatCurrency(totalBillAmount),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // GARIS PUTUS-PUTUS 2
                DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp)) // Jarak setelah garis

                // LABEL Teks: Tujuan Pembayaran
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tujuan pembayaran",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Wallet(splitBillData.payer)

                Spacer(modifier = Modifier.height(16.dp)) // Jarak sebelum garis

                DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp)) // Jarak setelah garis

                Anggota(processedMembers, ::formatCurrency)

                }
            } // End of Card

            } // End of Box Wrappper

            Spacer(modifier = Modifier.height(16.dp)) // Jarak antar elemen

        } // End of Column
        } // End of Box
    } // End of Scaffold
}

// FUNGSI UNTUK WALLET
@Composable
fun Wallet(payer: Member) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer) // Ganti Container biar kontras dikit
            .padding(16.dp),
        // 1. PENTING: Agar Logo BCA dan Teks di kanannya rata tengah secara vertikal
        verticalAlignment = Alignment.CenterVertically
    ) {

        // --- GAMBAR LOGO ---
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)) // Curve dikit di logo
                .background(MaterialTheme.colorScheme.primary)
                .padding(4.dp), // Padding dalam biar logo gak nempel pinggir putih
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Wallet,
                contentDescription = "Logo Wallet1",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        // 2. SPACER: Memberi jarak antara Logo dan Teks (biar gak nempel)
        Spacer(modifier = Modifier.width(16.dp))

        // --- KOLOM TEKS ---
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            // Nama Pemilik
            Text(
                text = payer.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )

            // Baris Nomor & Icon Copy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = payer.phoneNumber ?: "-",
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.CopyAll,
                    contentDescription = "Copy",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(payer.phoneNumber ?: ""))
                            scope.launch {
                                // You can add snackbar here if needed
                            }
                        }
                )
            }
        }
    }
}

// FUNGSI UNTUK MENGATUR RINCIAN ANGGOTA
@Composable
fun Anggota(
    processedMembers: List<ProcessedMember>,
    currencyFormatter: (Int) -> String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // A. HEADLINE with member count
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Anggota (${processedMembers.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // B. LIST ANGGOTA (Wrapping tiap anggota)
        processedMembers.forEachIndexed { index, pm ->
            MemberItemRow(pm, currencyFormatter)
            if (index < processedMembers.size - 1) { // Only add divider if not the last item
                Spacer(modifier = Modifier.height(8.dp))
                DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// --- 3. KOMPONEN ITEM PER ORANG (Biar rapi) ---
@Composable
fun MemberItemRow(
    data: ProcessedMember,
    currencyFormatter: (Int) -> String
) {
    // State untuk buka-tutup rincian
    var isExpanded by remember { mutableStateOf(false) }

    // Hitung inisial & warna (Disamakan dari SplitBillScreen)
    val initial = remember(data.member.name) { data.member.name.firstOrNull()?.uppercase() ?: "?" }
    val avatarColor = remember(data.member.name) { generateColorForName(data.member.name) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        // BARIS 1: Header (Icon + Nama + NoHP + Harga)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Sejajar vertikal dengan icon dan text column
        ) {
            // KIRI: Icon Avatar (Initial + Random Color)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // KANAN: Detail Info (Nama & Harga)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Baris Nama & Harga Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Kolom Nama & Badge Pembuat
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = data.member.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            if (data.isPayer) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Pembuat",
                                    color = Color(0xFF00C853), // Warna Hijau
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        // No HP
                        Text(
                            text = data.member.phoneNumber ?: "-",
                            color = Color.Black.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 2.dp) // Sedikit padding biar sejajar tinggi icon
                        )
                    }

                    // Harga Total (Kanan Atas)
                    Text(
                        text = currencyFormatter(data.totalToPay),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }

        // BARIS 2: Tombol Toggle Rincian (Full Width, Mentok Kiri)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rincian pesanan",
                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.8f)
            )
            // Panah berubah arah
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Black.copy(alpha = 0.8f)
            )
        }

        // BARIS 3: Area Rincian (Full Width)
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                data.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "x${item.qty}",
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Text(
                            text = currencyFormatter(item.sharePrice),
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp)) // Jarak antar elemen

    }
}


// ============================================
// KHUSUS LAYOUT CETAK (TIDAK TAMPIL DI UI UTAMA)
// ============================================
@Composable
fun CaptureLayout(
    splitBillData: SplitBillData,
    processedMembers: List<ProcessedMember>,
    totalBillAmount: Int,
    transactionDate: String? = null,
    capturedImage: ImageBitmap? = null,
    formatCurrency: (Int) -> String,
    graphicsLayer: androidx.compose.ui.graphics.layer.GraphicsLayer,
    onCaptured: (ImageBitmap) -> Unit
) {
    // Gunakan Box yang digambar ke GraphicsLayer
    Box(
        modifier = Modifier
            .width(400.dp) // Lebar fix agar hasil cetak konsisten
            .wrapContentHeight() // Tinggi menyesuaikan konten
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawContent()
            }
            // Background putih solid wajib untuk hasil cetak
            .background(Color.White) 
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.wrapContentHeight(),
            horizontalAlignment = Alignment.Start
        ) {
             // 1. Header Kiri (Split Bill Text)
             Text("Split Bill", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
             Spacer(Modifier.height(16.dp))

             // 2. Center Info (Gambar + Merchant + Total)
             Column(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 // Gambar Struk (Prioritaskan gambar asli, fallback ke placeholder)
                 if (capturedImage != null) {
                     Image(
                         bitmap = capturedImage,
                         contentDescription = "Gambar struk",
                         contentScale = ContentScale.Crop,
                         modifier = Modifier
                             .size(80.dp)
                             .clip(RoundedCornerShape(10.dp))
                             .background(Color.White)
                     )
                 } else {
                     Image(
                         painter = painterResource(Res.drawable.header_rincian),
                         contentDescription = "Gambar struk",
                         contentScale = ContentScale.Crop,
                         modifier = Modifier
                             .size(80.dp)
                             .clip(RoundedCornerShape(10.dp))
                             .background(Color.White)
                     )
                 }
                 Spacer(Modifier.height(8.dp))
                 
                 Text(splitBillData.merchantName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                 Text(transactionDate ?: "-", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.8f))
                 
                 Spacer(Modifier.height(16.dp))
                 
                 DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))
                 
                 // Total Biaya
                 Column(
                     modifier = Modifier.padding(vertical = 10.dp),
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     Text("Total Jumlah", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.7f))
                     Text(
                        text = formatCurrency(totalBillAmount),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                     )
                 }
                 
                 DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))
             }

             Spacer(Modifier.height(16.dp))

             // 3. Tujuan Pembayaran
             Text("Tujuan pembayaran", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
             Spacer(Modifier.height(16.dp))
             Wallet(splitBillData.payer)
             
             Spacer(Modifier.height(16.dp))
             DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))
             Spacer(Modifier.height(16.dp))

             // 4. List Anggota (Compact View)
             Text("Anggota (${processedMembers.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
             Spacer(Modifier.height(16.dp))

             processedMembers.forEachIndexed { index, pm ->
                 ItemCetakCompact(pm, formatCurrency)
                 if (index < processedMembers.size - 1) {
                     Spacer(Modifier.height(8.dp))
                     DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))
                     Spacer(Modifier.height(8.dp))
                 }
             }
        }
    }

    // Trigger onCaptured setelah drawing selesai (Frame berikutnya)
    LaunchedEffect(Unit) {
        // Beri sedikit delay agar layout sempat ter-render
        kotlinx.coroutines.delay(100) 
        val bitmap = graphicsLayer.toImageBitmap()
        onCaptured(bitmap)
    }
}

// Item Anggota Versi Ringkas (Khusus Cetak)
@Composable
fun ItemCetakCompact(
    data: ProcessedMember,
    currencyFormatter: (Int) -> String
) {
    val initial = data.member.name.firstOrNull()?.uppercase() ?: "?"
    val avatarColor = generateColorForName(data.member.name)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(initial.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        
        Spacer(Modifier.width(12.dp))
        
        // Nama
        Text(
            data.member.name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        // Total
        Text(
            currencyFormatter(data.totalToPay),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

// FUNGSI KHUSUS UNTUK MEMBUAT GARIS PUTUS-PUTUS
@Composable
fun DashedDivider(
    color: Color = Color.Gray,
    thickness: Float = 6f,
    dashLength: Float = 10f,
    gapLength: Float = 10f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = thickness,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
        )
    }
}

// Helper Generator Warna Avatar (Agar konsisten dengan nama) - Disamakan dari SplitBillScreen
fun generateColorForName(name: String): Color {
    val colors = listOf(
        Color(0xFF00897B), Color(0xFF1976D2), Color(0xFFE53935),
        Color(0xFFFB8C00), Color(0xFF8E24AA), Color(0xFF43A047)
    )
    if (name.isEmpty()) return colors[0]
    return colors[name.first().uppercaseChar().code % colors.size]
}

@Preview
@Composable
fun DoneScreenPreview() {
    val payer = Member(id = "1", name = "Sena", wallet = "GoPay", phoneNumber = "08123456789")
    val member2 = Member(id = "2", name = "Budi")

    val splitBillData = SplitBillData(
        merchantName = "Warung Padang",
        payer = payer,
        members = listOf(member2),
        totalMembers = 3,
        membersWithPaymentInfo = 1
    )

    val items = listOf(
        AssignableBillItem(id = "1", name = "Nasi Rendang", price = 25000, qty = 1, assignedMemberIds = listOf("1")),
        AssignableBillItem(id = "2", name = "Es Teh Manis", price = 5000, qty = 3, assignedMemberIds = listOf("1", "2", "3")),
        AssignableBillItem(id = "3", name = "Kerupuk Kulit", price = 10000, qty = 1, assignedMemberIds = listOf("2"))
    )

    MaterialTheme {
        DoneScreen(
            splitBillData = splitBillData,
            assignedItems = items
        )
    }
}