package com.bagi_bill.bagi_bill.presentation.screens.rincian

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bagi_bill.composeapp.generated.resources.Res
import bagi_bill.composeapp.generated.resources.header_rincian
import com.bagi_bill.bagi_bill.domain.parser.ParsedReceipt
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme
import com.bagi_bill.bagi_bill.presentation.util.rememberBitmapFromBytes
import org.jetbrains.compose.resources.painterResource
import com.bagi_bill.bagi_bill.presentation.components.DraftConfirmationDialog

/**
 * RincianScreen - Displays parsed receipt data (read-only mode).
 * Features:
 * - Gradient header with curved bottom
 * - Dynamic TopAppBar that changes on scroll
 * - Store name input
 * - Info tambahan via Bottom Sheet
 * - Thumbnail foto struk
 * - List item dari OCR
 * - Summary (Subtotal, Pajak, Servis, Diskon, Lainnya, Total)
 * - Action buttons: "Ubah rincian" & "Konfirmasi"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RincianScreen(
    parsedReceipt: ParsedReceipt,
    imageBytes: ByteArray? = null,
    onBack: () -> Unit,
    onExitToHome: () -> Unit = {},
    onSaveDraft: () -> Unit = {},
    onRetakePhoto: () -> Unit = {},
    onEditDetails: () -> Unit = {},
    onConfirm: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // State
    var splitBillName by remember { mutableStateOf(parsedReceipt.name) }
    var additionalInfo by remember { mutableStateOf("") }
    var showDrawer by remember { mutableStateOf(false) }
    var tempAdditionalInfo by remember { mutableStateOf(additionalInfo) }
    val drawerState = rememberModalBottomSheetState()
    
    // Draft dialog state
    var showDraftDialog by remember { mutableStateOf(false) }

    // Convert ByteArray to ImageBitmap
    val capturedImage: ImageBitmap? = rememberBitmapFromBytes(imageBytes)

    // Scroll state for dynamic TopAppBar
    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemScrollOffset > 50 || listState.firstVisibleItemIndex > 0 }
    }
    
    // Draft confirmation dialog
    DraftConfirmationDialog(
        showDialog = showDraftDialog,
        onDismiss = { showDraftDialog = false },
        onSaveDraft = {
            showDraftDialog = false
            onSaveDraft()
        },
        onDiscard = {
            showDraftDialog = false
            onExitToHome()
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // HEADER IMAGE (with curved bottom)
        Image(
            painter = painterResource(Res.drawable.header_rincian),
            contentDescription = "Header",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.TopCenter)
                .clip(bottomArcShape(curveMagnitude = 40.dp))
        )

        // SCAFFOLD
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Crossfade(
                    targetState = isScrolled,
                    animationSpec = tween(durationMillis = 300),
                    label = "TopBarAnimation"
                ) { scrolled ->
                    if (scrolled) {
                        // SCROLLED: White background, black icons
                        TopAppBar(
                            windowInsets = WindowInsets.statusBars,
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                            navigationIcon = {
                                IconButton(onClick = { showDraftDialog = true }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black)
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
                                    modifier = Modifier.padding(start = AppTheme.Spacing.small)
                                )
                            },
                            actions = {
                                IconButton(onClick = {}) {
                                    Icon(Icons.AutoMirrored.Filled.Help, "Bantuan", tint = Color.Gray)
                                }
                            }
                        )
                    } else {
                        // NOT SCROLLED: Transparent background, white circular buttons
                        TopAppBar(
                            windowInsets = WindowInsets.statusBars,
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                            navigationIcon = {
                                WhiteCircleIconButton(
                                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                                    onClick = { showDraftDialog = true },
                                    contentDescription = "Back",
                                    modifier = Modifier.padding(start = AppTheme.Spacing.large)
                                )
                            },
                            title = {},
                            actions = {
                                WhiteCircleIconButton(
                                    icon = Icons.AutoMirrored.Filled.Help,
                                    onClick = {},
                                    contentDescription = "Bantuan",
                                    iconTint = Color.Gray,
                                    modifier = Modifier.padding(end = AppTheme.Spacing.large)
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
                            elevation = AppTheme.Elevation.bottomSticky,
                            clip = false,
                            spotColor = Color.Black,
                            ambientColor = Color.Black
                        ),
                    color = Color.White,
                    shape = AppTheme.Shapes.bottomSheet
                ) {
                    Column(
                        modifier = Modifier.padding(
                            start = AppTheme.Spacing.large,
                            end = AppTheme.Spacing.large,
                            top = AppTheme.Spacing.xLarge,
                            bottom = AppTheme.Spacing.xLarge
                        )
                    ) {
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(AppTheme.Size.buttonHeight),
                            enabled = parsedReceipt.items.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                                disabledContentColor = Color.White
                            ),
                            shape = AppTheme.Shapes.pill
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
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // Spacer for header overlap
                item { Spacer(modifier = Modifier.height(140.dp)) }

                // CARD 1: Nama Split Bill
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.Spacing.large)
                            .offset(y = (-30).dp),
                        shape = AppTheme.Shapes.card,
                        colors = CardDefaults.cardColors(containerColor = AppTheme.Colors.cardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.Elevation.none)
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                vertical = AppTheme.Spacing.large,
                                horizontal = AppTheme.Spacing.large
                            )
                        ) {
                            Text(
                                "Nama Split Bill",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            TextField(
                                value = splitBillName,
                                onValueChange = { splitBillName = it },
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                ),
                                placeholder = { Text("Kasih nama split bill disini") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = Color.LightGray
                                )
                            )
                            Spacer(modifier = Modifier.height(AppTheme.Spacing.xxLarge))

                            // Info Tambahan Button
                            Surface(
                                onClick = {
                                    tempAdditionalInfo = additionalInfo
                                    showDrawer = true
                                },
                                color = Color(0xFFF5F5F5),
                                shape = AppTheme.Shapes.pill,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = AppTheme.Spacing.large),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(AppTheme.Spacing.small))
                                    Text(
                                        text = if (additionalInfo.isEmpty()) {
                                            "Masukkin info tambahan di sini"
                                        } else {
                                            if (additionalInfo.length > 35) additionalInfo.take(35) + ".."
                                            else additionalInfo
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

                // CARD 2: Foto Struk
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.Spacing.large),
                        shape = AppTheme.Shapes.card,
                        colors = CardDefaults.cardColors(containerColor = AppTheme.Colors.cardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.Elevation.none)
                    ) {
                        Column(modifier = Modifier.padding(AppTheme.Spacing.large)) {
                            Text(
                                text = "Struk berhasil di-scan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(AppTheme.Spacing.xSmall))
                            Text(
                                text = "Klik gambar di bawah buat liat foto struk lebih jelas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Thumbnail
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
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                // Button Foto Ulang
                                OutlinedButton(
                                    onClick = onRetakePhoto,
                                    shape = AppTheme.Shapes.pill,
                                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppTheme.Size.iconSmall)
                                    )
                                    Spacer(modifier = Modifier.width(AppTheme.Spacing.small))
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

                // CARD 3: Rincian Item + Summary
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.Spacing.large)
                            .padding(top = AppTheme.Spacing.large, bottom = AppTheme.Spacing.xxLarge),
                        shape = AppTheme.Shapes.card,
                        colors = CardDefaults.cardColors(containerColor = AppTheme.Colors.cardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.Elevation.none)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // LIST ITEMS
                            parsedReceipt.items.forEach { item ->
                                BillItemRow(
                                    name = item.name,
                                    qty = item.qty,
                                    price = item.price
                                )
                            }

                            Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

                            // Divider
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )

                            Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

                            // SUMMARY
                            BillSummaryRow("Subtotal", formatPrice(parsedReceipt.summary.subtotal))
                            BillSummaryRow("Pajak", formatPrice(parsedReceipt.summary.pajak))
                            BillSummaryRow("Servis", formatPrice(parsedReceipt.summary.servis))
                            BillSummaryRow("Diskon", formatPrice(parsedReceipt.summary.diskon))
                            BillSummaryRow("Lainnya", formatPrice(parsedReceipt.summary.lainnya))

                            Spacer(modifier = Modifier.height(AppTheme.Spacing.small))

                            // TOTAL
                            BillSummaryRow("Jumlah total", formatPrice(parsedReceipt.summary.total), isTotal = true)

                            Spacer(modifier = Modifier.height(AppTheme.Spacing.xLarge))

                            // UBAH RINCIAN BUTTON
                            OutlinedButton(
                                onClick = onEditDetails,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp),
                                shape = AppTheme.Shapes.pill,
                                border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(AppTheme.Size.iconSmall),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(AppTheme.Spacing.small))
                                Text("Ubah rincian", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // MODAL BOTTOM SHEET: Info Tambahan
        if (showDrawer) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDrawer = false
                    tempAdditionalInfo = additionalInfo
                },
                sheetState = drawerState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.Spacing.large)
                        .padding(bottom = AppTheme.Spacing.xxLarge)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            showDrawer = false
                            tempAdditionalInfo = additionalInfo
                        }) {
                            Icon(Icons.Default.Close, "Tutup", tint = Color.Black)
                        }
                        Text(
                            text = "Catatan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        TextButton(onClick = { tempAdditionalInfo = "" }) {
                            Text(
                                text = "Hapus",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

                    Text(
                        text = "Masukkin info tambahan di sini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(AppTheme.Spacing.small))

                    OutlinedTextField(
                        value = tempAdditionalInfo,
                        onValueChange = { tempAdditionalInfo = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = { Text("Tulis catatan di sini...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 8
                    )

                    Spacer(modifier = Modifier.height(AppTheme.Spacing.small))

                    Text(
                        text = "${tempAdditionalInfo.length}/160",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(AppTheme.Spacing.xLarge))

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
                        shape = AppTheme.Shapes.pill
                    ) {
                        Text(text = "Selesai", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==================== HELPER COMPOSABLES ====================

@Composable
private fun WhiteCircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    iconTint: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(40.dp),
        shape = androidx.compose.foundation.shape.CircleShape,
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BillItemRow(name: String, qty: Int, price: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppTheme.Spacing.small),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = name.uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = "x$qty",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.15f)
        )
        Text(
            text = formatPrice(price),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.25f)
        )
    }
}

@Composable
private fun BillSummaryRow(label: String, price: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppTheme.Spacing.xSmall),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isTotal) Color.Black else Color.Gray,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = price,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ==================== HELPER FUNCTIONS ====================

private fun formatPrice(price: Int): String {
    if (price == 0) return "0"
    return price.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}

@Composable
private fun bottomArcShape(curveMagnitude: Dp = 40.dp): Shape {
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
