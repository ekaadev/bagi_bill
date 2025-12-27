package com.bagi_bill.bagi_bill

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bagi_bill.bagi_bill.ui.screens.rincian.RincianScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * OptIn annotation untuk menggunakan API eksperimental dari Material3.
 * Preview annotation untuk melihat ui di IDE.
 * Composable annotation untuk menandai fungsi sebagai UI Composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen() {

    // 1. STATE: Pengatur navigasi antara Home dan Camera
    var showCamera by remember { mutableStateOf(false) }
    var showRincian by remember { mutableStateOf(false) }
    var showUbahRincian by remember { mutableStateOf(false) }

    // State untuk menyimpan hasil OCR
    var ocrResult by remember { mutableStateOf<ParsedReceipt?>(null) }
    var capturedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    // State lain
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val textService = TextRecognitionService()

    // TODO: ambil dari database jumlah item draft
    val counterItemInDraft = 3
    /**
     * ============================================================================
     * [LANGKAH 1] TITIK AWAL ALUR KAMERA - HomeScreen.kt
     * ============================================================================
     *
     * Ketika user menekan tombol FAB (FloatingActionButton) di bawah layar:
     * → State `showCamera` berubah dari false ke true
     * → Kondisi if(showCamera) terpenuhi
     * → CameraScreen() dipanggil dan ditampilkan
     *
     * CameraScreen memiliki 2 callback:
     * 1. onExit → dipanggil saat user ingin kembali (tekan tombol back)
     * 2. onPhotoConfirmed → dipanggil saat user selesai mengambil & mengkonfirmasi foto
     *    Parameter `bytes` adalah ByteArray yang berisi data gambar (JPEG/PNG)
     *
     * LANJUT KE: CameraScreen.kt untuk melihat alur selanjutnya →
     * ============================================================================
     */
    if (showCamera) {
        // === MODE KAMERA ===
        // Memanggil CameraScreen (lihat file: CameraScreen.kt)
        CameraScreen(
            // Callback: dipanggil saat user tekan tombol back di kamera
            onExit = { showCamera = false },

            // Callback: dipanggil saat user mengkonfirmasi foto yang diambil
            // `bytes` adalah hasil akhir berupa ByteArray (data gambar mentah)
            onPhotoConfirmed = { bytes ->
                // ============================================================
                // [LANGKAH TERAKHIR] HASIL GAMBAR DITERIMA DI SINI
                // ============================================================
                // `bytes` adalah ByteArray yang berisi data gambar
                // Bisa digunakan untuk:
                // - Upload ke server
                // - Simpan ke database lokal
                // - Proses OCR untuk membaca struk
                // - Konversi ke ImageBitmap untuk ditampilkan
                // ============================================================

                println("Hasil foto diterima di Home: ${bytes.size} bytes")

                // Simpan gambar
                capturedImageBytes = bytes

                scope.launch {
                    try {
                        val extractedText = textService.recognizeText(bytes)
                        val result = parserUtil(extractedText)

                        // Simpan hasil OCR
                        ocrResult = result

                        // Tutup kamera dan tampilkan RincianScreen
                        showCamera = false
                        showRincian = true

                        println("xyz: $result")

                        println("OCR berhasil: ${result.items.size} items ditemukan")
                    } catch (e: Exception) {
                        println("OCR Error: ${e.message}")
                        e.printStackTrace()
                        showCamera = false

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Gagal memproses gambar: ${e.message}",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
            }
        )
    } else if (showRincian && ocrResult != null) {
        // === MODE RINCIAN ===
        // Menampilkan hasil OCR di RincianScreen
        RincianScreen(
            parsedReceipt = ocrResult!!,
            imageBytes = capturedImageBytes,
            onBack = {
                showRincian = false
                ocrResult = null
                capturedImageBytes = null
            },
            onRetakePhoto = {
                // Tutup rincian dan buka kamera untuk foto ulang
                showRincian = false
                showCamera = true
                // Data OCR dan gambar lama akan diganti dengan yang baru setelah foto ulang
            },
            onEditDetails = {
                // Navigasi ke halaman Ubah Rincian
                showRincian = false
                showUbahRincian = true
            }
        )
    } else if (showUbahRincian && ocrResult != null) {
        // === MODE UBAH RINCIAN ===
        // Menampilkan halaman edit rincian
        // TODO: Implement UbahRincianScreen
    } else {
        // Scaffold, sebagaia kanvas dasar layout pada material design.
        // Fungsi ini otomatis mengatur ruang untuk UI bawaan dari OS (misalnya status bar, navigation bar)
        Scaffold(
            // snackbarHost untuk menampilkan snackbar
            snackbarHost = { SnackbarHost( hostState = snackbarHostState)},
            // modifier untuk mengatur tampilan dan behavior dari layout
            modifier = Modifier
                .fillMaxSize() // mengisi seluruh ruang yang tersedia
                .nestedScroll(scrollBehavior.nestedScrollConnection), // Hubungkan scroll konten ke TopBar
            topBar = {
                TopAppBar(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    // Background TopBar
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background, // Sesuaikan warna background
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    // Icon Profil (kiri)
                    navigationIcon = {
                    },
                    // Status Bar (tengah)
                    // Row di dalam Row untuk menampung elemen-elemen di tengah
                    title = {
                        // Row untuk menyusun elemen di dalamnya secara horizontal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Title
                            Text(
                                text = "Bagi Bill",
                                color = Color.Black,
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Button draft
                            Surface(
                                onClick = {
                                    // TODO: fitur button history draft
                                },
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 2.dp,
                                tonalElevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Draft ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "($counterItemInDraft)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    },

                    // Icon Bantuan (kanan)
                    actions = {
                        IconButton(onClick = {
                            /* TODO: Aksi Bantuan */
                            scope.launch {
                                snackbarHostState.showSnackbar("Fungsi Bantuan belum tersedia")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Help,
                                contentDescription = "Bantuan",
                                tint = Color.Gray
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Content yang bisa di-scroll
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 100.dp) // Berikan padding agar konten tidak tertutup bottom bar
                ) {
                    // Group Creation Section
                    WalletSection()

                    // Manual Input Bill Section
                    HomeManualInputBillScreen()

                    // History Section
                    HomeHistoryScreen()
                }
                // Bottom bar yang ter-pin di bawah
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 32.dp, // Shadow dari Surface
                    tonalElevation = 0.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 24.dp,
                                bottom = 24.dp
                            )
                    ) {
                        Button(
                            onClick = {
                                scope.launch { showCamera = true }
                            },
                            modifier = Modifier.fillMaxWidth().height(43.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Scan Sekarang",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WalletSection() {
    val gradientPurple = Brush.verticalGradient(
        colors = listOf(
            Color.White,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
        )
    )

    // Container Wallet Section
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        // Container Column di dalam Card
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Row, sebagai card header
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dompet kamu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                /**
                 * Surface, sebuah container pembungkus tombol "lihat semua"
                 * Memiliki behavior onClick
                 */
                Surface(
                    onClick = {
                        /* TODO: fitur lihat semua */
                    },
                    shape = RoundedCornerShape(100),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    // Tombol panah ke kanan
                    Row(
                        modifier = Modifier
                            .background(
                                brush = gradientPurple,
                                shape = RoundedCornerShape(100)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lihat semua",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width((4.dp)))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Spacer
            Spacer(modifier = Modifier.height(12.dp))

            // Section content card (atur wallet sendiri
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                color = Color.Transparent,
                onClick = { }
            ) {
                // Icon(kiri) + Text(title, description) (kanan)
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Wallet,
                        contentDescription = "Wallet",
                        modifier = Modifier
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(8.dp)
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(35))
                            .padding(4.dp),
                        tint = Color.White
                    )

                    // spacer
                    Spacer(modifier = Modifier.width(12.dp))

                    // text container
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Atur walletmu sendiri",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Buat wallet untuk simpan dana patungan biar lebih praktis.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // spacer
                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Wallet",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCreateGroupScreen() {
    val dummyContacts = listOf("Andi", "Budi", "Citra", "Dedi", "Eka", "Fani")

    val gradientPurple = Brush.verticalGradient(
        colors = listOf(
            Color.White,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
        )
    )

    /**
     * Card
     * Sebagai container pada section pembuatan grup
     */
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        // Container Column di dalam Card
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Row, sebagai card header
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Buat grup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                /**
                 * Surface, sebuah container pembungkus tombol "lihat semua"
                 * Memiliki behavior onClick
                 */
                Surface(
                    onClick = {
                        /* TODO: fitur lihat semua */
                    },
                    shape = RoundedCornerShape(100),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    // Tombol panah ke kanan
                    Row(
                        modifier = Modifier
                            .background(
                                brush = gradientPurple,
                                shape = RoundedCornerShape(100)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lihat semua",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width((4.dp)))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Spacer
            Spacer(modifier = Modifier.height(12.dp))

            // Row, sebagai container untuk content pembuatan grup
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TODO: tambahkan kontak deskripsi dibawah text "Kontakmu"
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Kontakmu",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Pilih bestie atau bikin grup biar patungan makin sat-set",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ContactAvatarStack(avatars = dummyContacts)
                }
            }
        }
    }
}

@Composable
fun HomeManualInputBillScreen() {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bikin baru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pilih cara yang kamu mau",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal
                )
            }

            // Spacer
            Spacer(modifier = Modifier.height(12.dp))

            // Section content card (atur jumlah sendiri)
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                color = Color.Transparent,
                onClick = { }
            ) {
                // Icon(kiri) + Text(title, description) (kanan)
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CallSplit,
                        contentDescription = "Manual Input Bill",
                        modifier = Modifier
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(8.dp)
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(35))
                            .padding(4.dp),
                        tint = Color.White
                    )

                    // spacer
                    Spacer(modifier = Modifier.width(12.dp))

                    // text container
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Atur jumlahnya sendiri",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Lebih cepat buat bagi rata, gak usah pake struk.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // spacer
                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Manual Input Bill",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHistoryScreen() {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        // Container dalam Card (paling awal)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Title card
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yang terakhir kamu buat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            // Spacer
            Spacer(modifier = Modifier.height(12.dp))

            // Section content card
            ListHistorySplitBill()
        }
    }
}

@Composable
fun ContactAvatarStack(
    avatars: List<String>,
    maxAvatars: Int = 3,
    modifier: Modifier = Modifier
) {
    // Hitung berapa avatar yang akan ditampilkan
    val displayCount = minOf(maxAvatars, avatars.size)
    val remaining = avatars.size - maxAvatars
    val avatarSize = 40.dp
    val overlapAmount = 12.dp

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(-overlapAmount)
    ) {
        // loop untuk menampilkan avatar
        for (i in 0 until displayCount) {
            Surface(
                modifier = Modifier
                    .size(avatarSize)
                    .border(2.dp, Color.White, CircleShape)
                    .zIndex((displayCount - i).toFloat()),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Di sini nanti Image() asli. Sementara pakai Text inisial.
                    Text(
                        text = avatars[i].take(1),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // indikator kontak avatar lebih
        if (remaining > 0) {
            Surface(
                modifier = Modifier
                    .size(avatarSize)
                    .border(2.dp, Color.White, CircleShape)
                    .zIndex(0f),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+$remaining",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun ListHistorySplitBill() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        // item history split bill
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.Transparent,
            shape = RoundedCornerShape(16.dp),
            onClick = {
                /* TODO: FITUR ITEM HISTORY */
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CallSplit,
                    contentDescription = "Manual Input Bill",
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
                        .padding(8.dp)
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(35))
                        .padding(4.dp),
                    tint = Color.White
                )

                // spacer
                Spacer(modifier = Modifier.width(12.dp))

                // Text Container
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    Text(
                        text = "Wizzmie",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }

                // price
                Text(
                    text = "Rp84.000",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }


        // lihat selengkapnya
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.Transparent,
            shape = RoundedCornerShape(50.dp),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
            onClick = {
                /* TODO: FITUR Lihat Selengkapnya */
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lihat selengkapnya",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Lihat selengkapnya",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}