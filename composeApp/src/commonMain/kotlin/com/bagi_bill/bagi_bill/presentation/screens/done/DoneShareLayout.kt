package com.bagi_bill.bagi_bill.presentation.screens.done

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bagi_bill.composeapp.generated.resources.Res
import bagi_bill.composeapp.generated.resources.header_rincian
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.SplitBillData
import com.bagi_bill.bagi_bill.presentation.screens.splitbill.generateColorForName
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

/**
 * Capture Layout for generating shareable image
 */
@Composable
fun CaptureLayout(
    splitBillData: SplitBillData,
    processedMembers: List<ProcessedMember>,
    totalBillAmount: Int,
    transactionDate: String,
    capturedImage: ImageBitmap?,
    formatCurrency: (Int) -> String,
    graphicsLayer: GraphicsLayer,
    onCaptured: (ImageBitmap) -> Unit
) {
    Box(
        modifier = Modifier
            .width(400.dp)
            .wrapContentHeight()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.Shapes.card,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Store Icon
                    if (capturedImage != null) {
                        Image(
                            bitmap = capturedImage,
                            contentDescription = "Struk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(AppTheme.Shapes.card)
                                .background(Color.White)
                        )
                    } else {
                        Image(
                            painter = painterResource(Res.drawable.header_rincian),
                            contentDescription = "Struk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(AppTheme.Shapes.card)
                                .background(Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Store Name
                    Text(
                        text = splitBillData.merchantName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    // Transaction Date
                    Text(
                        text = transactionDate.ifEmpty { "-" },
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider()
                    
                    // Total
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Biaya",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatCurrency(totalBillAmount),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    DashedDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Destination
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Tujuan pembayaran",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Wallet Info - Compact
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppTheme.Shapes.card,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD54F)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "💳", fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = splitBillData.payer.wallet ?: "Wallet",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = splitBillData.payer.phoneNumber ?: "-",
                                    fontSize = 12.sp,
                                    color = Color.Black.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "a/n ${splitBillData.payer.name}",
                                    fontSize = 11.sp,
                                    color = Color.Black.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Members Section
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Anggota",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Members - Compact
                    processedMembers.forEach { member ->
                        ItemCetakCompact(
                            member = member,
                            formatCurrency = formatCurrency
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Watermark
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Dibuat dengan BagiBill",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Capture after render
    LaunchedEffect(Unit) {
        delay(150)
        val bitmap = graphicsLayer.toImageBitmap()
        onCaptured(bitmap)
    }
}

/**
 * Compact member item for share image
 */
@Composable
private fun ItemCetakCompact(
    member: ProcessedMember,
    formatCurrency: (Int) -> String
) {
    val avatarColor = generateColorForName(member.name)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppTheme.Shapes.card,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.name.first().uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Name
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        if (member.isPayer) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = Color(0xFF4CAF50),
                                shape = AppTheme.Shapes.pill
                            ) {
                                Text(
                                    text = "Pembuat",
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Amount
                Text(
                    text = formatCurrency(member.totalToPay),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Items list
            if (member.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                member.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.name} x${item.qty}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatCurrency(item.sharePrice),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


