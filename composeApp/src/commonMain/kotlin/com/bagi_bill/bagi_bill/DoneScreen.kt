package com.bagi_bill.bagi_bill

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import bagi_bill.composeapp.generated.resources.Res
import bagi_bill.composeapp.generated.resources.logo_bca
import com.bagi_bill.bagi_bill.utils.decodeByteArrayToImageBitmap
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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DoneScreen(
    imageBytes: ByteArray? = null,
    onNavigateToRincian: () -> Unit = {},
    onGoHome: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

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

    // Data for sharing
    val memberList = listOf(
        DummyMember(
            name = "John Doe",
            phone = "+62",
            isCreator = true,
            total = "Rp300.000",
            items = listOf(
                DummyItem("Nasi Goreng Spesial", 1, "Rp45.000"),
                DummyItem("Es Jeruk", 1, "Rp15.000")
            )
        ),
        DummyMember(
            name = "John Doesn't",
            phone = "+62",
            isCreator = false,
            total = "Rp200.000",
            items = listOf(
                DummyItem("NASI PUTIH", 1, "Rp190.000"),
                DummyItem("Lainnya", 1, "Rp10.000")
            )
        ),
    )

    fun generateShareText(): String {
        val sb = StringBuilder()
        sb.appendLine("📋 RINCIAN SPLIT BILL")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("🏪 Toko: Toko Batik")
        sb.appendLine("📅 Tanggal: 12/2/2005 - 19:30 PM")
        sb.appendLine("💰 Total: Rp500.000")
        sb.appendLine()
        sb.appendLine("🏦 BANK TUJUAN:")
        sb.appendLine("Bank: BCA")
        sb.appendLine("No. Rekening: 874294")
        sb.appendLine("A/n: John Doe")
        sb.appendLine()
        sb.appendLine("👥 ANGGOTA (${memberList.size}):")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━")
        memberList.forEach { member ->
            sb.appendLine("• ${member.name} ${if (member.isCreator) "(Pembuat)" else ""}")
            sb.appendLine("  Bayar: ${member.total}")
            sb.appendLine("  Items:")
            member.items.forEach { item ->
                sb.appendLine("    - ${item.name} x${item.qty} = ${item.price}")
            }
            sb.appendLine()
        }
        return sb.toString()
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
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Fungsi Profile belum tersedia")
                        }
                    }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = Color.White, shape = RoundedCornerShape(100))
                                .padding(6.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .background(Color.White, shape = RoundedCornerShape(25))
                            .padding(vertical = 6.dp, horizontal = 12.dp),
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
                                .background(color = Color.White, shape = RoundedCornerShape(100))
                                .padding(6.dp),
                            tint = Color.Gray,
                        )
                    }

                    IconButton(onClick = {
                        scope.launch {
                            clipboardManager.setText(AnnotatedString(generateShareText()))
                            snackbarHostState.showSnackbar("Rincian berhasil disalin ke clipboard!")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = Color.White, shape = RoundedCornerShape(100))
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
                                .background(color = Color.White, shape = RoundedCornerShape(100))
                                .padding(6.dp),
                            tint = Color.Gray,
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->

// KOTAK PEMBUNGKUS UTAMA (LAYAR)
        Column(
            modifier = Modifier
                .padding(paddingValues) // PENTING: Turunkan konten di bawah TopBar
                .fillMaxSize()
                .padding(16.dp) // Jarak dari tepi layar HP
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // KOTAK KARTU (STRUK/BILL)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colorScheme.primary)
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
                            .width(80.dp)
                            .height(120.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                    )
                } 
                // else {
                //     Image(
                //         painter = painterResource(Res.drawable.foto_struk_belanja_10),
                //         contentDescription = "Gambar struk",
                //         contentScale = ContentScale.Crop,
                //         modifier = Modifier
                //             .width(80.dp)
                //             .height(120.dp)
                //             .clip(RoundedCornerShape(10.dp))
                //             .background(Color.White) // Opsional: biar gambar menonjol
                //     )
                // }

                Spacer(modifier = Modifier.height(8.dp)) // Jarak antar elemen

                // Nama Toko
                Text(
                    text = "Toko Batik",
                    color = MaterialTheme.colorScheme.onPrimary, // Warna teks kontras
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Tanggal
                Text(
                    text = "12/2/2005 - 19:30 PM",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp)) // Jarak sebelum garis

                // GARIS PUTUS-PUTUS 1
                DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))

                // BAGIAN 2: HARGA
                Text(
                    text = "Rp500.000",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 10.dp) // Jarak 10dp atas bawah dari divider
                )

                // GARIS PUTUS-PUTUS 2
                DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp)) // Jarak setelah garis

                Wallet()

                Spacer(modifier = Modifier.height(16.dp)) // Jarak sebelum garis

                DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp)) // Jarak setelah garis

                Anggota(memberList)

            }
        }
    }
}

// FUNGSI UNTUK WALLET
// --- 1. MODEL DATA DUMMY (Langsung di sini) ---
data class DummyMember(
    val name: String,
    val phone: String,
    val isCreator: Boolean,
    val total: String,
    val items: List<DummyItem>
)

data class DummyItem(
    val name: String,
    val qty: Int,
    val price: String
)

@Composable
fun Wallet() {
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
        Image(
            painter = painterResource(Res.drawable.logo_bca),
            contentDescription = "Logo Bank",
            contentScale = ContentScale.Fit, // Fit biar logo utuh
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)) // Curve dikit di logo
                .background(Color.White)
                .padding(4.dp) // Padding dalam biar logo gak nempel pinggir putih
        )

        // 2. SPACER: Memberi jarak antara Logo dan Teks (biar gak nempel)
        Spacer(modifier = Modifier.width(16.dp))

        // --- KOLOM TEKS ---
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            // Nama Pemilik
            Text(
                text = "John Doe",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // Baris Nomor & Icon Copy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "87429476677888",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.CopyAll,
                    contentDescription = "Copy",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            clipboardManager.setText(AnnotatedString("87429409999999999"))
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
fun Anggota(memberList: List<DummyMember> = listOf(
    DummyMember(
        name = "John Doe",
        phone = "+62",
        isCreator = true,
        total = "Rp300.000",
        items = listOf(
            DummyItem("Nasi Goreng Spesial", 1, "Rp45.000"),
            DummyItem("Es Jeruk", 1, "Rp15.000")
        )
    ),
    DummyMember(
        name = "John Doesn't",
        phone = "+62",
        isCreator = false,
        total = "Rp200.000",
        items = listOf(
            DummyItem("NASI PUTIH", 1, "Rp190.000"),
            DummyItem("Lainnya", 1, "Rp10.000")
        )
    ),
)) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // A. HEADLINE with member count
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Anggota (${memberList.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // B. LIST ANGGOTA (Wrapping tiap anggota)
        memberList.forEachIndexed { index, member ->
            MemberItemRow(member)
            if (index < memberList.size - 1) { // Only add divider if not the last item
                Spacer(modifier = Modifier.height(16.dp))
                DashedDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// --- 3. KOMPONEN ITEM PER ORANG (Biar rapi) ---
@Composable
fun MemberItemRow(data: DummyMember) {
    // State untuk buka-tutup rincian
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // KIRI: Icon Avatar
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.onPrimary // Changed from Color.Black
        )

        Spacer(modifier = Modifier.width(12.dp))

        // KANAN: Detail Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Baris 1: Nama & Harga Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Kolom Nama & Badge Pembuat
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = data.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary // Added consistent color
                        )
                        if (data.isCreator) {
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
                        text = data.phone,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), // Changed from Color.Gray
                        fontSize = 14.sp
                    )
                }

                // Harga Total (Kanan Atas)
                Text(
                    text = data.total,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary // Added consistent color
                )
            }

            // Baris 2: Tombol Toggle Rincian
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded } // KLIK DI SINI
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rincian pesanan",
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) // Added consistent color
                )
                // Panah berubah arah
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) // Added consistent color
                )
            }

            // Baris 3: Area Rincian (Hidden/Shown)
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
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f), // Changed from Color.Gray
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "x${item.qty}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f), // Changed from Color.Gray
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Text(
                                text = item.price,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f) // Changed from Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// FUNGSI UNTUK MENGHAPUS (ga dipake)
// fun Hapus() {}

// FUNGSI KHUSUS UNTUK MEMBUAT GARIS PUTUS-PUTUS
@Composable
fun DashedDivider(
    color: Color = Color.Gray,
    thickness: Float = 2f,
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