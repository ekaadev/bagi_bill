package com.bagi_bill.bagi_bill.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bagi_bill.bagi_bill.model.BillItem


// KOMPONEN BARIS ITEM (Atomic UI)
@Composable
fun BillItemRow(item: BillItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        verticalAlignment = Alignment.Top // Rata atas jika nama barang panjang (2 baris)
    ) {
        // KOLOM 1: NAMA BARANG
        Text(
            text = item.name.uppercase(), // Sesuai desain (Huruf besar semua)
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.weight(0.6f)
        )

        // KOLOM 2: QTY
        Text(
            text = "x${item.qty}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.15f)
        )

        // KOLOM 3: HARGA
        Text(
            text = "${item.price}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.25f)
        )
    }
}

// 3. KOMPONEN BARIS SUMMARY (Subtotal, Pajak, Total)
@Composable
fun BillSummaryRow(
    label: String,
    price: String,
    isTotal: Boolean = false // Flag khusus untuk baris "Jumlah Total"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
            color = if (isTotal) Color.Black else Color.Black,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}