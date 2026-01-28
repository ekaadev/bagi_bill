package com.bagi_bill.bagi_bill.presentation.screens.done

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import bagi_bill.composeapp.generated.resources.Res
import bagi_bill.composeapp.generated.resources.header_rincian
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.SplitBillData
import com.bagi_bill.bagi_bill.presentation.screens.splitbill.AssignableBillItem
import com.bagi_bill.bagi_bill.presentation.screens.splitbill.generateColorForName
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme
import com.bagi_bill.bagi_bill.presentation.util.rememberBitmapFromBytes
import com.bagi_bill.bagi_bill.utils.rememberShareHelper
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/**
 * Data class for processed member with their share
 */
data class ProcessedMember(
    val id: String,
    val name: String,
    val phoneNumber: String?,
    val isPayer: Boolean,
    val items: List<ProcessedItem>,
    val totalToPay: Int
)

data class ProcessedItem(
    val name: String,
    val qty: Int,
    val sharePrice: Int
)

/**
 * DoneScreen - Final screen showing split bill summary
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneScreen(
    splitBillData: SplitBillData,
    assignedItems: List<AssignableBillItem>,
    imageBytes: ByteArray? = null,
    transactionDate: String = "",
    onNavigateToRincian: () -> Unit = {},
    onGoHome: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var showImageDialog by remember { mutableStateOf(false) }

    val shareHelper = rememberShareHelper()
    val graphicsLayer = rememberGraphicsLayer()

    // Convert ByteArray to ImageBitmap
    val capturedImage: ImageBitmap? = rememberBitmapFromBytes(imageBytes)

    // State for capture trigger
    var triggerCapture by remember { mutableStateOf(false) }

    // Currency formatter
    fun formatCurrency(amount: Int): String {
        val reversed = amount.toString().reversed()
        val chunked = reversed.chunked(3).joinToString(".")
        return "Rp${chunked.reversed()}"
    }

    // Process members with their shares
    val processedMembers = remember(splitBillData, assignedItems) {
        val allMembers = listOf(splitBillData.payer) + splitBillData.members

        allMembers.map { member ->
            val myItems = assignedItems.filter { it.assignedMemberIds.contains(member.id) }
                .map { item ->
                    val sharePrice = item.getShareForMember(member.id)
                    ProcessedItem(
                        name = item.name,
                        qty = item.qty,
                        sharePrice = sharePrice
                    )
                }

            val totalPay = myItems.sumOf { it.sharePrice }

            ProcessedMember(
                id = member.id,
                name = member.name,
                phoneNumber = member.phoneNumber,
                isPayer = member.id == splitBillData.payer.id,
                items = myItems,
                totalToPay = totalPay
            )
        }
    }

    val totalBillAmount = remember(assignedItems) {
        assignedItems.sumOf { it.totalPrice }
    }

    // Image Dialog
    if (showImageDialog && capturedImage != null) {
        Dialog(
            onDismissRequest = { showImageDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showImageDialog = false }
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = capturedImage,
                    contentDescription = "Full Image",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight()
                        .clip(AppTheme.Shapes.card)
                        .clickable(enabled = false) {}
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
                    scrolledContainerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onGoHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(AppTheme.Size.iconMedium),
                            tint = Color.Black
                        )
                    }
                },
                title = {
                    Text(
                        text = "Rincian split bill",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                },
                actions = {
                    // Edit Button
                    IconButton(onClick = onNavigateToRincian) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(AppTheme.Size.iconMedium),
                            tint = Color.Gray
                        )
                    }

                    // Share Button
                    IconButton(onClick = { triggerCapture = true }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(AppTheme.Size.iconMedium),
                            tint = Color.Gray
                        )
                    }

                    // Help Button
                    IconButton(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Fungsi Bantuan belum tersedia")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Bantuan",
                            modifier = Modifier.size(AppTheme.Size.iconMedium),
                            tint = Color.Gray
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            // Sticky Home Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = AppTheme.Elevation.bottomSticky),
                color = Color.White,
                shape = AppTheme.Shapes.bottomSheet
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = AppTheme.Spacing.large,
                        vertical = AppTheme.Spacing.xLarge
                    )
                ) {
                    Button(
                        onClick = onGoHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.Size.buttonHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = AppTheme.Shapes.pill
                    ) {
                        Text(
                            text = "Kembali ke Beranda",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Capture Layout (Hidden)
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
                            triggerCapture = false
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

            // Main Content
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = AppTheme.Spacing.large)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

                // Bill Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppTheme.Shapes.card,
                    colors = CardDefaults.cardColors(containerColor = AppTheme.Colors.cardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.dp, AppTheme.Colors.borderLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Receipt Image
                        if (capturedImage != null) {
                            Image(
                                bitmap = capturedImage,
                                contentDescription = "Gambar struk",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(AppTheme.Shapes.card)
                                    .background(Color.White)
                                    .clickable { showImageDialog = true }
                            )
                        } else {
                            Image(
                                painter = painterResource(Res.drawable.header_rincian),
                                contentDescription = "Gambar struk",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(AppTheme.Shapes.card)
                                    .background(Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.height(AppTheme.Spacing.small))

                        // Store Name
                        Text(
                            text = splitBillData.merchantName,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        // Date
                        Text(
                            text = transactionDate.ifEmpty { "Tanggal tidak tersedia" },
                            color = Color.Black.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(AppTheme.Spacing.large))
                        DashedDivider()

                        // Total Amount
                        Column(
                            modifier = Modifier.padding(vertical = AppTheme.Spacing.medium),
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

                        DashedDivider()
                        Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

                        // Payment Destination Label
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Tujuan pembayaran",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

                        // Wallet Card
                        WalletCard(
                            payer = splitBillData.payer,
                            snackbarHostState = snackbarHostState
                        )

                        Spacer(modifier = Modifier.height(AppTheme.Spacing.large))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

                        // Members List
                        MembersList(
                            processedMembers = processedMembers,
                            formatCurrency = ::formatCurrency
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.Spacing.large))
            }
        }
    }
}
