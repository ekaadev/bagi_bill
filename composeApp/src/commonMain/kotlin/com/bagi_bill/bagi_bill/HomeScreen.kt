package com.bagi_bill.bagi_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Preview // Preview annotation untuk melihat ui di IDE
@Composable // Composable, digunakan untuk membuat fungsi UI
fun HomeScreen() {
    // Scroll behavior untuk animasi top bar (opsional, tapi bagus buat UX)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Scaffold, sebagaia kanvas dasar layout pada material design.
    // Fungsi ini otomatis mengatur ruang untuk UI bawaan dari OS (misalnya status bar, navigation bar)
    Scaffold(
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
                    IconButton(onClick = {
                        /* TODO: Aksi Profil */
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil",
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = Color.White, shape = RoundedCornerShape(100))
                                .padding(1.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },

                // Status Bar (tengah)
                // Row di dalam Row untuk menampung elemen-elemen di tengah
                title = {
                    // Row untuk menyusun elemen di dalamnya secara horizontal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Trik agar container status ada di tengah-tengah sisa ruang
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .background(Color.White, shape = RoundedCornerShape(25))
                            .padding(vertical = 6.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFFD4AF37),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Good | App Bagi Bill",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp,
                            color = Color.Black
                        )
                    }
                },

                // Icon Bantuan (kanan)
                actions = {
                    IconButton(onClick = {
                        /* TODO: Aksi Bantuan */
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Padding otomatis dari Scaffold agar tidak ketutup TopBar
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Area Konten lainnya
            Text("Konten di sini", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun HomeCreateGroupScreen() {

}

@Composable
fun HomeManualInputBillScreen() {

}

@Composable
fun HomeHistoryScreen() {

}