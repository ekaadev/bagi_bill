package com.bagi_bill.bagi_bill

/**
 * SELECT MEMBER SCREEN
 * ====================
 *
 * Screen untuk memilih anggota yang akan ikut dalam split bill.
 *
 * CARA PENGGUNAAN:
 * ---------------
 * SelectMemberScreen(
 *     onNavigateToSplitBill = { splitBillData ->
 *         // splitBillData berisi semua data yang dibutuhkan
 *         // Kirim ke Split Bill Screen melalui navigation
 *         navController.navigate("split_bill", splitBillData)
 *     }
 * )
 *
 * DATA YANG DIKIRIM (SplitBillData):
 * ---------------------------------
 * - payer: Member                     -> Yang nalangin (wajib punya wallet & phone)
 * - members: List<Member>             -> Anggota lainnya
 * - totalMembers: Int                 -> Total semua member (payer + members)
 * - membersWithPaymentInfo: Int       -> Jumlah member dengan wallet & phone
 *
 * HELPER FUNCTIONS:
 * ----------------
 * - getAllMembers()        -> Mendapatkan semua member termasuk payer
 * - getEligiblePayers()    -> Member yang bisa jadi payer (punya wallet & phone)
 * - isValid()              -> Validasi minimal 2 member dan payer valid
 *
 * CONTOH DATA:
 * -----------
 * SplitBillData(
 *     payer = Member("Sena", "GoPay", "081234567890"),
 *     members = [
 *         Member("Budi", "OVO", "082345678901"),
 *         Member("Ani", null, null),  // Tidak punya wallet/phone
 *         Member("Citra", "DANA", "083456789012")
 *     ],
 *     totalMembers = 4,
 *     membersWithPaymentInfo = 3
 * )
 */

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import kotlin.random.Random
import androidx.compose.animation.*
import androidx.compose.animation.core.*

/**
 * DATA CLASS: Member
 * ==================
 * Menyimpan informasi anggota yang ikut dalam split bill.
 *
 * PROPERTIES:
 * - id: String              -> Unique ID (auto-generated)
 * - name: String            -> Nama anggota (REQUIRED)
 * - wallet: String?         -> E-Wallet yang digunakan (OPTIONAL)
 * - phoneNumber: String?    -> Nomor tujuan pembayaran (OPTIONAL)
 *
 * ATURAN:
 * - Nama wajib diisi
 * - Wallet dan phone number optional
 * - Member bisa jadi payer hanya jika punya wallet DAN phone number
 * - Member tanpa wallet/phone hanya bisa jadi anggota biasa
 */
data class Member(
    val id: String = Random.nextInt(100000, 999999).toString(),
    val name: String,
    val wallet: String? = null,
    val phoneNumber: String? = null
) {
    // Helper function untuk cek apakah member bisa jadi payer
    fun canBePayer(): Boolean = !wallet.isNullOrBlank() && !phoneNumber.isNullOrBlank()
}

/**
 * DATA CLASS: SplitBillData
 * =========================
 * Payload yang siap dikirim ke Split Bill Screen.
 * Berisi semua data yang dibutuhkan untuk split bill.
 *
 * PROPERTIES:
 * - payer: Member                     -> Yang nalangin (WAJIB punya wallet & phone)
 * - members: List<Member>             -> Anggota lainnya
 * - totalMembers: Int                 -> Total semua member (payer + members)
 * - membersWithPaymentInfo: Int       -> Jumlah member dengan wallet & phone
 *
 * FACTORY METHOD:
 * SplitBillData.create(payer, members) -> Otomatis hitung totalMembers & membersWithPaymentInfo
 *
 * HELPER FUNCTIONS:
 * - getAllMembers()       -> List semua member termasuk payer
 * - getEligiblePayers()   -> List member yang bisa jadi payer
 * - isValid()             -> Validasi data (minimal 2 member, payer valid)
 *
 * CONTOH:
 * val data = SplitBillData.create(
 *     payer = Member("Sena", "GoPay", "081234567890"),
 *     members = listOf(
 *         Member("Budi", "OVO", "082345678901"),
 *         Member("Ani", null, null)
 *     )
 * )
 * // data.totalMembers = 3
 * // data.membersWithPaymentInfo = 2
 */
data class SplitBillData(
    val payer: Member,                    // Member yang nalangin
    val members: List<Member>,            // List anggota lainnya
    val totalMembers: Int,                // Total semua member (payer + members)
    val membersWithPaymentInfo: Int       // Jumlah member yang punya wallet & phone number
) {
    companion object {
        fun create(payer: Member, members: List<Member>): SplitBillData {
            val allMembers = listOf(payer) + members
            val withPaymentInfo = allMembers.count { it.canBePayer() }

            return SplitBillData(
                payer = payer,
                members = members,
                totalMembers = allMembers.size,
                membersWithPaymentInfo = withPaymentInfo
            )
        }
    }

    // Helper untuk mendapatkan semua member (termasuk payer)
    fun getAllMembers(): List<Member> = listOf(payer) + members

    // Helper untuk mendapatkan member yang eligible untuk jadi payer
    fun getEligiblePayers(): List<Member> = getAllMembers().filter { it.canBePayer() }

    // Validate data sebelum dikirim
    fun isValid(): Boolean {
        return payer.canBePayer() && totalMembers >= 2 // Minimal payer + 1 member
    }
}

// List wallet yang sering digunakan di Indonesia
val indonesianWallets = listOf(
    "GoPay",
    "OVO",
    "DANA",
    "ShopeePay",
    "LinkAja",
    "Jenius",
    "BCA Mobile",
    "Mandiri e-Money",
    "BRI Mobile",
    "BNI Mobile"
)

/**
 * SELECT MEMBER SCREEN - MAIN COMPOSABLE
 * ======================================
 *
 * CARA PENGGUNAAN DI NAVIGATION:
 * -----------------------------
 * // Di file navigation Anda
 * composable("select_member") {
 *     SelectMemberScreen(
 *         onNavigateToSplitBill = { splitBillData ->
 *             // Kirim data ke Split Bill Screen
 *             navController.navigate(
 *                 route = "split_bill",
 *                 args = splitBillData  // atau serialize jika perlu
 *             )
 *         }
 *     )
 * }
 *
 * PARAMETER:
 * ---------
 * @param onNavigateToSplitBill: (SplitBillData) -> Unit
 *        Callback yang dipanggil saat tombol "Konfirmasi" diklik.
 *        Mengirim SplitBillData yang sudah lengkap dan tervalidasi.
 *
 * SPLIT BILL DATA STRUCTURE:
 * -------------------------
 * data class SplitBillData(
 *     payer: Member,                     // Yang nalangin
 *     members: List<Member>,             // Anggota lainnya
 *     totalMembers: Int,                 // Total semua member
 *     membersWithPaymentInfo: Int        // Member dengan wallet & phone
 * )
 *
 * CONTOH DATA YANG DIKIRIM:
 * ------------------------
 * SplitBillData(
 *     payer = Member(
 *         id = "123456",
 *         name = "Sena",
 *         wallet = "GoPay",
 *         phoneNumber = "081234567890"
 *     ),
 *     members = [
 *         Member("234567", "Budi", "OVO", "082345678901"),
 *         Member("345678", "Ani", null, null),        // Tidak ada wallet/phone
 *         Member("456789", "Citra", "DANA", "083456789012")
 *     ],
 *     totalMembers = 4,                  // 1 payer + 3 members
 *     membersWithPaymentInfo = 3         // Sena, Budi, Citra (Ani tidak punya)
 * )
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SelectMemberScreen(
    onNavigateToSplitBill: (SplitBillData) -> Unit = { }
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // State untuk menyimpan list member
    val members = remember {
        mutableListOf<Member>().toMutableStateList()
    }

    // Member yang bayar (fixed member)
    var payerMember by remember {
        mutableStateOf(Member(name = "Sena", wallet = "GoPay", phoneNumber = "081234567890"))
    }

    // State untuk navigasi ke screen replacement
    var showReplacementScreen by remember { mutableStateOf(false) }

    // Base Frame Select Member Screen dengan animasi zoom
    AnimatedContent(
        targetState = showReplacementScreen,
        transitionSpec = {
            if (targetState) {
                // Zoom in untuk forward navigation
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(150, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(150)) togetherWith
                        scaleOut(
                            targetScale = 1.1f,
                            animationSpec = tween(150, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(150))
            } else {
                // Zoom out untuk back navigation
                scaleIn(
                    initialScale = 1.1f,
                    animationSpec = tween(150, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(150)) togetherWith
                        scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(150, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(150))
            }
        },
        label = "ScreenTransition"
    ) { isReplacementScreen ->
        if (isReplacementScreen) {
            // Show replacement screen
            ReplacePayerScreen(
                currentPayer = payerMember,
                members = members,
                onBack = { showReplacementScreen = false },
                onSelectPayer = { selectedMember ->
                    // Swap: current payer becomes regular member, selected member becomes payer
                    val oldPayer = payerMember.copy()
                    payerMember = selectedMember

                    // Remove selected member from list and add old payer
                    members.remove(selectedMember)
                    if (oldPayer.id != selectedMember.id) {
                        members.add(oldPayer)
                    }

                    showReplacementScreen = false
                }
            )
        } else {
            // Show main member selection screen
            SelectMemberContent(
                scrollBehavior = scrollBehavior,
                payerMember = payerMember,
                members = members,
                onAddMember = { newMember ->
                    members.add(newMember)
                },
                onRemoveMember = { memberId ->
                    val memberToRemove = members.find { it.id == memberId }
                    if (memberToRemove != null) {
                        members.remove(memberToRemove)
                    }
                },
                onReplacePayer = {
                    showReplacementScreen = true
                },
                onNavigateToSplitBill = {
                    // ============================================
                    // SIAPKAN DATA UNTUK SPLIT BILL SCREEN
                    // ============================================
                    // Data ini sudah terstruktur dan siap dikirim:
                    // - payer: Member yang nalangin
                    // - members: List anggota lainnya
                    // - totalMembers: Total semua member
                    // - membersWithPaymentInfo: Member dengan wallet & phone

                    val splitBillData = SplitBillData.create(
                        payer = payerMember,
                        members = members.toList()
                    )

                    // Kirim data ke Split Bill Screen via navigation
                    onNavigateToSplitBill(splitBillData)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMemberContent(
    scrollBehavior: TopAppBarScrollBehavior,
    payerMember: Member,
    members: List<Member>,
    onAddMember: (Member) -> Unit,
    onRemoveMember: (String) -> Unit,
    onReplacePayer: () -> Unit,
    onNavigateToSplitBill: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                           // TODO: Handle back navigation
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pilih anggota",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // TODO: Handle help action
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Help,
                            contentDescription = "Bantuan",
                            tint = Color.Gray
                        )
                    }
                }
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
                // section untuk list member
                MemberSection(
                    payerMember = payerMember,
                    members = members,
                    onAddMember = onAddMember,
                    onRemoveMember = onRemoveMember,
                    onReplacePayer = onReplacePayer
                )
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
                            // Navigate to Split Bill Screen dengan membawa data
                            onNavigateToSplitBill()
                        },
                        modifier = Modifier.fillMaxWidth().height(43.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
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
    }
}

// Screen untuk memilih replacement payer
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplacePayerScreen(
    currentPayer: Member,
    members: List<Member>,
    onBack: () -> Unit,
    onSelectPayer: (Member) -> Unit
) {
    var selectedMemberId by remember { mutableStateOf<String?>(null) }

    // Filter members yang eligible (punya wallet dan phone number)
    val eligibleMembers = members.filter { it.canBePayer() }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                title = {
                    Text(
                        text = "Siapa yang nalangin?",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Show help */ }) {
                        Icon(
                            imageVector = Icons.Filled.Help,
                            contentDescription = "Bantuan",
                            tint = Color.Gray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                // Section: Current payer using GoPay
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Yang nalangin sekarang",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Current payer as option
                        PayerOptionItem(
                            member = currentPayer,
                            isSelected = selectedMemberId == currentPayer.id,
                            onSelect = { selectedMemberId = currentPayer.id }
                        )
                    }
                }

                // Section: Other eligible members
                if (eligibleMembers.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Pilih dari anggota lain",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            eligibleMembers.forEach { member ->
                                PayerOptionItem(
                                    member = member,
                                    isSelected = selectedMemberId == member.id,
                                    onSelect = { selectedMemberId = member.id }
                                )

                                if (member != eligibleMembers.last()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Bottom button
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                shadowElevation = 32.dp,
                tonalElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 24.dp
                    )
                ) {
                    Button(
                        onClick = {
                            selectedMemberId?.let { id ->
                                val selectedMember = if (id == currentPayer.id) {
                                    currentPayer
                                } else {
                                    eligibleMembers.find { it.id == id }
                                }
                                selectedMember?.let { onSelectPayer(it) }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(43.dp),
                        enabled = selectedMemberId != null,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
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
    }
}

// Component untuk option item dengan radio button
@Composable
fun PayerOptionItem(
    member: Member,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        color = when {
                            member.name.first().isLetter() -> {
                                val colors = listOf(
                                    Color(0xFF00897B),
                                    Color(0xFF1976D2),
                                    Color(0xFFE53935),
                                    Color(0xFFFB8C00),
                                    Color(0xFF8E24AA),
                                    Color(0xFF43A047)
                                )
                                colors[member.name.first().uppercaseChar().code % colors.size]
                            }
                            else -> Color(0xFF00897B)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "${member.name} (Kamu)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = member.phoneNumber ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Radio button
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = Color.Gray
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSection(
    payerMember: Member,
    members: List<Member>,
    onAddMember: (Member) -> Unit,
    onRemoveMember: (String) -> Unit,
    onReplacePayer: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var selectedWallet by remember { mutableStateOf(indonesianWallets[0]) }
    var expandedWallet by remember { mutableStateOf(false) }

    // Generate random colors for avatars
    val avatarColors = listOf(
        Color(0xFF00897B),
        Color(0xFF1976D2),
        Color(0xFFE53935),
        Color(0xFFFB8C00),
        Color(0xFF8E24AA),
        Color(0xFF43A047),
        Color(0xFF3949AB),
        Color(0xFF00ACC1)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Fixed "Bayar ke" section
            Column {
                Text(
                    text = "Bayar ke",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Fixed member item
                MemberItem(
                    name = payerMember.name,
                    wallet = payerMember.wallet,
                    subtitle = payerMember.phoneNumber,
                    showReplaceButton = true,
                    onReplaceClick = onReplacePayer
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // Scrollable "Anggota" section
            Column {
                Text(
                    text = "Anggota",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal scrollable list
                if (members.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        members.forEachIndexed { index, member ->
                            MemberAvatar(
                                name = member.name,
                                backgroundColor = avatarColors[index % avatarColors.size],
                                onRemove = { onRemoveMember(member.id) },
                                onClick = { /* TODO: Handle member detail view */ }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Belum ada anggota ditambahkan",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Opsi penambahan anggota",
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )
            // Button add member diluar kontak
            Surface(
                color = Color.Transparent,
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                onClick = {
                    showBottomSheet = true
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonSearch,
                        contentDescription = "Person search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Di luar kontak",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // Bottom Sheet
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                        // Reset form
                        name = TextFieldValue("")
                        phoneNumber = TextFieldValue("")
                        selectedWallet = indonesianWallets[0]
                    },
                    sheetState = sheetState,
                ) {
                    // Content Bottom Sheet
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 32.dp)
                    ) {
                        // Title
                        Text(
                            text = "Tambah anggota",
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Nama Field
                        Text(
                            text = "Nama",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // TextField dengan zero padding
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Black
                            ),
                            decorationBox = { innerTextField ->
                                Column {
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (name.text.isEmpty()) {
                                            Text(
                                                text = "Masukkan nama",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.Gray
                                            )
                                        }
                                        innerTextField()
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Wallet Dropdown
                        Text(
                            text = "E-Wallet",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedWallet,
                            onExpandedChange = { expandedWallet = !expandedWallet }
                        ) {
                            OutlinedTextField(
                                value = selectedWallet,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expandedWallet,
                                onDismissRequest = { expandedWallet = false }
                            ) {
                                indonesianWallets.forEach { wallet ->
                                    DropdownMenuItem(
                                        text = { Text(wallet) },
                                        onClick = {
                                            selectedWallet = wallet
                                            expandedWallet = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Phone Number Field
                        Text(
                            text = "Nomor Tujuan",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        BasicTextField(
                            value = phoneNumber,
                            onValueChange = {
                                // Only allow numbers
                                if (it.text.all { char -> char.isDigit() }) {
                                    phoneNumber = it
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),
                            decorationBox = { innerTextField ->
                                Column {
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (phoneNumber.text.isEmpty()) {
                                            Text(
                                                text = "08123456789",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.Gray
                                            )
                                        }
                                        innerTextField()
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Button add member
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            enabled = name.text.isNotBlank(),
                            onClick = {
                                // Create new member
                                val newMember = Member(
                                    name = name.text.trim(),
                                    wallet = if (selectedWallet.isNotBlank() && phoneNumber.text.isNotBlank())
                                        selectedWallet else null,
                                    phoneNumber = if (phoneNumber.text.isNotBlank() && selectedWallet.isNotBlank())
                                        phoneNumber.text.trim() else null
                                )
                                onAddMember(newMember)

                                // Close bottom sheet and reset form
                                showBottomSheet = false
                                name = TextFieldValue("")
                                phoneNumber = TextFieldValue("")
                                selectedWallet = indonesianWallets[0]
                            }
                        ) {
                            Text(
                                text = "Tambahin ke anggota",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Info text
                        Text(
                            text = "💡 Wallet dan nomor tujuan opsional. Member tanpa data ini tidak bisa jadi yang nalangin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// Component untuk fixed member item dengan tombol ganti
@Composable
fun MemberItem(
    name: String,
    wallet: String?,
    subtitle: String?,
    showReplaceButton: Boolean = false,
    onReplaceClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = Color(0xFF00897B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (wallet != null) {
                        Text(
                            text = wallet,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (wallet != null && subtitle != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        if (showReplaceButton) {
            Surface(
                color = Color.Transparent,
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                onClick = onReplaceClick
            ) {
                Text(
                    text = "Ganti",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Component untuk avatar member yang bisa di-scroll dengan tombol X untuk remove
@Composable
fun MemberAvatar(
    name: String,
    backgroundColor: Color,
    onRemove: () -> Unit,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(70.dp) // Berikan lebar tetap agar nama tidak terpotong
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(color = backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Remove button (X icon)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color = Color.Black)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove member",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Nama di bawah avatar
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}