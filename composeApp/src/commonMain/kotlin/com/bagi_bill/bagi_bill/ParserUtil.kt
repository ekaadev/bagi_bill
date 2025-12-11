package com.bagi_bill.bagi_bill

// Data class untuk item
data class ReceiptItem(
    val qty: Int,
    val name: String,
    val price: Int
)

// Data class untuk summary
data class ReceiptSummary(
    val subtotal: Int = 0,          // Total harga jumlah item
    val pajak: Int = 0,             // Pajak jika ada
    val diskon: Int = 0,            // Diskon jika ada
    val lainnya: Int = 0,           // Balance jika ada perhitungan tidak sesuai
    val total: Int = 0              // Jumlah keseluruhan
)

// Data class untuk hasil parsing
data class ParsedReceipt(
    val name: String,               // Nama struk (dari teks paling awal)
    val items: List<ReceiptItem>,
    val summary: ReceiptSummary
)

fun parserUtil(text: String): ParsedReceipt {

    val lines = text.split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    // === HASIL PARSING ===
    var receiptName = ""
    val items = mutableListOf<ReceiptItem>()

    // Summary values
    var subtotal = 0
    var pajak = 0
    var diskon = 0
    var lainnya = 0
    var total = 0

    // === REGEX PATTERNS ===

    // Pattern untuk item dengan qty dan harga di satu baris: "1 Nasi Goreng 25.000" atau "2x Ayam Bakar Rp30,000"
    val itemFullRegex = Regex("^([0-9]+)\\s*[xX]?\\s+(.+?)\\s+(?:Rp\\s?)?([0-9]{1,3}(?:[.,][0-9]{3})+|[0-9]+)$")

    // Pattern untuk item dengan qty tapi tanpa harga: "1 Bread Butter Pudding"
    val itemNoPrice = Regex("^([0-9]+)\\s+([A-Za-z][A-Za-z0-9 ]+)$")

    // Pattern untuk harga saja: "11,500" atau "43.500" atau "Rp 25.000"
    val priceOnlyRegex = Regex("^(?:Rp\\s?)?([0-9]{1,3}(?:[.,][0-9]{3})+)$")

    // Pattern untuk summary dengan harga di baris yang sama: "Total: 43,500"
    val summaryWithPriceRegex = Regex("^([A-Za-z][A-Za-z ]*?)\\s*:\\s*(?:Rp\\s?)?([0-9]{1,3}(?:[.,][0-9]{3})+|[0-9]+)$")

    // Pattern untuk label summary tanpa harga: "Subtotal :" atau "Total:"
    val summaryLabelRegex = Regex("^([A-Za-z][A-Za-z ]*?)\\s*:$")

    // Keywords untuk mencocokkan summary
    val subtotalKeywords = listOf("subtotal", "sub total", "sub-total", "harga pesanan")
    val pajakKeywords = listOf("pajak", "tax", "ppn", "pb1", "service")
    val diskonKeywords = listOf("diskon", "discount", "potongan", "promo", "voucher")
    val totalKeywords = listOf("total", "grand total", "jumlah", "amount", "bayar", "payment")

    // Temporary storage untuk item yang belum punya harga
    val pendingItems = mutableListOf<Pair<Int, String>>() // qty, name

    // Temporary storage untuk label summary yang belum punya harga (multiple labels)
    val pendingSummaryLabels = mutableListOf<String>()

    // Helper function untuk mengkategorikan summary
    fun categorizeSummary(label: String, price: Int) {
        val lowerLabel = label.lowercase()
        when {
            subtotalKeywords.any { lowerLabel.contains(it) } -> subtotal = price
            pajakKeywords.any { lowerLabel.contains(it) } -> pajak += price // accumulate pajak
            diskonKeywords.any { lowerLabel.contains(it) } -> diskon += price // accumulate diskon
            totalKeywords.any { lowerLabel.contains(it) } -> total = price
            else -> lainnya += price // Kategori lainnya
        }
        println("[Parser] Categorized '$label' = $price")
    }

    for ((index, line) in lines.withIndex()) {
        // Skip baris yang terlalu pendek
        if (line.length < 2) continue

        // === AMBIL NAMA STRUK (dari baris awal yang bukan angka/tanggal) ===
        if (receiptName.isEmpty() && index < 5) {
            // Skip jika baris adalah angka, tanggal, atau terlalu pendek
            val isNumericOrDate = line.matches(Regex("^[0-9/\\-:. ]+$"))
            val isLikelyName = line.length >= 3 &&
                               !line.startsWith("Check") &&
                               !line.startsWith("No") &&
                               !line.contains("POS") &&
                               line.any { it.isLetter() }

            if (!isNumericOrDate && isLikelyName) {
                receiptName = line
                println("[Parser] Receipt name: $receiptName")
                continue
            }
        }

        // 1. Cek apakah item lengkap (qty + nama + harga)
        val fullMatch = itemFullRegex.find(line)
        if (fullMatch != null) {
            val qty = fullMatch.groupValues[1].toIntOrNull() ?: 1
            val name = fullMatch.groupValues[2].trim()
            val priceStr = fullMatch.groupValues[3].replace(".", "").replace(",", "")
            val price = priceStr.toIntOrNull() ?: 0

            if (name.isNotEmpty() && price > 0) {
                items.add(ReceiptItem(qty, name, price))
                println("[Parser] Full item: $qty x $name = $price")
                continue
            }
        }

        // 2. Cek apakah summary dengan harga
        val summaryMatch = summaryWithPriceRegex.find(line)
        if (summaryMatch != null) {
            val label = summaryMatch.groupValues[1].trim()
            val priceStr = summaryMatch.groupValues[2].replace(".", "").replace(",", "")
            val price = priceStr.toIntOrNull() ?: 0

            if (price > 0) {
                categorizeSummary(label, price)
                continue
            }
        }

        // 3. Cek apakah harga saja (untuk dipasangkan dengan pending item/summary)
        val priceMatch = priceOnlyRegex.find(line)
        if (priceMatch != null) {
            val priceStr = priceMatch.groupValues[1].replace(".", "").replace(",", "")
            val price = priceStr.toIntOrNull() ?: 0

            if (price > 0) {
                // Prioritas 1: Jika ada pending item, pasangkan dengan harga
                if (pendingItems.isNotEmpty()) {
                    val (qty, name) = pendingItems.removeAt(0)
                    items.add(ReceiptItem(qty, name, price))
                    println("[Parser] Matched item: $qty x $name = $price")
                    continue
                }

                // Prioritas 2: Jika ada pending summary label, pasangkan
                if (pendingSummaryLabels.isNotEmpty()) {
                    val label = pendingSummaryLabels.removeAt(0)
                    categorizeSummary(label, price)
                    continue
                }
            }
        }

        // 4. Cek apakah item tanpa harga (qty + nama)
        val itemNoPriceMatch = itemNoPrice.find(line)
        if (itemNoPriceMatch != null) {
            val qty = itemNoPriceMatch.groupValues[1].toIntOrNull() ?: 1
            val name = itemNoPriceMatch.groupValues[2].trim()

            if (name.isNotEmpty() && name.length > 2) {
                pendingItems.add(Pair(qty, name))
                println("[Parser] Pending item (no price): $qty x $name")
                continue
            }
        }

        // 5. Cek apakah label summary tanpa harga
        val summaryLabelMatch = summaryLabelRegex.find(line)
        if (summaryLabelMatch != null) {
            val label = summaryLabelMatch.groupValues[1].trim()
            pendingSummaryLabels.add(label)
            println("[Parser] Pending summary label: $label")
            continue
        }
    }

    // === HITUNG SUBTOTAL OTOMATIS JIKA TIDAK ADA ===
    if (subtotal == 0 && items.isNotEmpty()) {
        subtotal = items.sumOf { it.qty * it.price }
        println("[Parser] Auto-calculated subtotal: $subtotal")
    }

    // === HITUNG LAINNYA (BALANCE) JIKA PERLU ===
    // Jika total dari struk berbeda dengan perhitungan, gunakan lainnya untuk balance
    if (total > 0 && subtotal > 0) {
        val calculatedTotal = subtotal + pajak - diskon
        if (calculatedTotal != total) {
            lainnya = total - calculatedTotal
            println("[Parser] Calculated 'lainnya' for balance: $lainnya")
        }
    }

    // === HITUNG TOTAL FINAL ===
    // Total = subtotal + pajak - diskon + lainnya
    val finalTotal = subtotal + pajak - diskon + lainnya
    println("[Parser] Final total calculation: $subtotal + $pajak - $diskon + $lainnya = $finalTotal")

    // === HASIL AKHIR ===
    val summaryResult = ReceiptSummary(
        subtotal = subtotal,
        pajak = pajak,
        diskon = diskon,
        lainnya = lainnya,
        total = finalTotal  // Total dihitung dari: subtotal + pajak - diskon + lainnya
    )

    val result = ParsedReceipt(
        name = receiptName,
        items = items.toList(),
        summary = summaryResult
    )

    return result
}