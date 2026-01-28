package com.bagi_bill.bagi_bill.domain.parser

/**
 * Parses raw OCR text into structured ParsedReceipt.
 * Optimized for Indonesian receipt formats.
 */
fun parseReceiptText(text: String): ParsedReceipt {
    val lines = text.split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    var receiptName = ""
    val items = mutableListOf<ReceiptItem>()

    // Summary temp values
    var subtotal = 0
    var pajak = 0
    var diskon = 0
    var lainnya = 0
    var total = 0
    var servis = 0

    // Pending item for multi-line detection
    var pendingItemName: String? = null
    var pendingItemQty: Int = 1

    // === REGEX PATTERNS ===
    
    // Pattern 1: Full item with qty, name, price
    // "2 x Nasi Goreng 25.000" or "2x Nasi Goreng Rp 25,000" or "2 Nasi Goreng 25000"
    val itemFullRegex = Regex(
        """(\d+)\s*[xX]?\s+(.+?)\s+(?:Rp\.?\s?|IDR\s?)?(\d{1,3}(?:[.,]\d{3})*|\d+)\s*$"""
    )
    
    // Pattern 2: Item without qty (assume qty = 1)
    // "Nasi Goreng 25.000" or "Nasi Goreng Rp25000"
    val itemNoPrefixQtyRegex = Regex(
        """^([A-Za-z].+?)\s+(?:Rp\.?\s?|IDR\s?)?(\d{1,3}(?:[.,]\d{3})*|\d+)\s*$"""
    )
    
    // Pattern 3: Qty + name only (price on next line)
    val itemNoPrice = Regex("""^(\d+)\s+([A-Za-z][A-Za-z0-9\s]+)$""")
    
    // Pattern 4: Price only (for multi-line items)
    val priceOnlyRegex = Regex("""^(?:Rp\.?\s?|IDR\s?)?(\d{1,3}(?:[.,]\d{3})+|\d{4,})\s*$""")
    
    // Pattern 5: Summary line with label and price
    val summaryRegex = Regex(
        """([A-Za-z][A-Za-z0-9%\s/-]*?)\s*:?\s*(?:Rp\.?\s?|IDR\s?)?(\d{1,3}(?:[.,]\d{3})*|\d+)\s*$"""
    )

    // Keywords
    val subtotalKeywords = listOf("subtotal", "sub total", "sub-total", "sub_total")
    val pajakKeywords = listOf("pajak", "tax", "ppn", "pb1", "vat", "pbk")
    val servisKeywords = listOf("service", "layanan", "servis", "sc", "svc")
    val diskonKeywords = listOf("diskon", "discount", "potongan", "promo", "voucher")
    val totalKeywords = listOf("total", "grand total", "jumlah", "bayar", "tunai", "cash", "amount")
    val skipKeywords = listOf(
        "terima kasih", "thank you", "struk", "receipt", "kasir", "no.", 
        "tanggal", "waktu", "date", "time", "meja", "table", "order",
        "npwp", "alamat", "address", "telp", "phone"
    )

    // Helper: parse price string to Int
    fun parsePrice(priceStr: String): Int {
        return priceStr.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
    }

    // Helper: check if line looks like a receipt name
    fun isLikelyName(line: String, index: Int): Boolean {
        if (index >= 5) return false
        if (line.length < 3) return false
        if (line.matches(Regex("""^[\d/\-:.\s]+$"""))) return false
        if (skipKeywords.any { line.lowercase().contains(it) }) return false
        // Likely a name if it's mostly uppercase letters
        val upperRatio = line.count { it.isUpperCase() }.toFloat() / line.length
        return upperRatio > 0.3 || line.length >= 5
    }

    // Helper: check if line is a summary keyword line
    fun containsSummaryKeyword(line: String): Boolean {
        val lower = line.lowercase()
        return subtotalKeywords.any { lower.contains(it) } ||
                pajakKeywords.any { lower.contains(it) } ||
                servisKeywords.any { lower.contains(it) } ||
                diskonKeywords.any { lower.contains(it) } ||
                totalKeywords.any { lower.contains(it) }
    }

    // Loop setiap baris teks
    for ((index, line) in lines.withIndex()) {
        if (line.length < 2) continue
        val lowerLine = line.lowercase()
        
        // Skip header/footer lines
        if (skipKeywords.any { lowerLine.contains(it) }) continue
        
        // Detect receipt name (first valid line)
        if (receiptName.isEmpty() && isLikelyName(line, index)) {
            receiptName = line
            continue
        }

        // Check for pending multi-line item
        if (pendingItemName != null) {
            val priceMatch = priceOnlyRegex.find(line)
            if (priceMatch != null) {
                val price = parsePrice(priceMatch.groupValues[1])
                if (price > 0) {
                    items.add(ReceiptItem(pendingItemQty, pendingItemName!!, price))
                }
                pendingItemName = null
                pendingItemQty = 1
                continue
            }
            // Not a price, reset pending
            pendingItemName = null
            pendingItemQty = 1
        }

        // Check for summary keywords first
        if (containsSummaryKeyword(line)) {
            val summaryMatch = summaryRegex.find(line)
            if (summaryMatch != null) {
                val label = summaryMatch.groupValues[1].lowercase().trim()
                val price = parsePrice(summaryMatch.groupValues[2])

                when {
                    subtotalKeywords.any { label.contains(it) } -> subtotal = price
                    pajakKeywords.any { label.contains(it) } -> pajak = price
                    servisKeywords.any { label.contains(it) } -> servis = price
                    diskonKeywords.any { label.contains(it) } -> diskon = price
                    totalKeywords.any { label.contains(it) } && price > total -> total = price
                }
            }
            continue
        }

        // Try Pattern 1: Full item (qty + name + price)
        val fullMatch = itemFullRegex.find(line)
        if (fullMatch != null) {
            val qtyStr = fullMatch.groupValues[1]
            val name = fullMatch.groupValues[2].trim()
            val priceStr = fullMatch.groupValues[3]
            
            val qty = qtyStr.toIntOrNull() ?: 1
            val price = parsePrice(priceStr)
            
            if (name.isNotBlank() && name.length >= 2 && price > 0) {
                items.add(ReceiptItem(qty, name, price))
                continue
            }
        }

        // Try Pattern 2: Item without prefix qty (name + price)
        val noPrefixMatch = itemNoPrefixQtyRegex.find(line)
        if (noPrefixMatch != null) {
            val name = noPrefixMatch.groupValues[1].trim()
            val priceStr = noPrefixMatch.groupValues[2]
            val price = parsePrice(priceStr)
            
            if (name.isNotBlank() && name.length >= 2 && price > 0) {
                items.add(ReceiptItem(1, name, price))
                continue
            }
        }

        // Try Pattern 3: Qty + name only (store for next line)
        val noPriceMatch = itemNoPrice.find(line)
        if (noPriceMatch != null) {
            val qty = noPriceMatch.groupValues[1].toIntOrNull() ?: 1
            val name = noPriceMatch.groupValues[2].trim()
            if (name.isNotBlank() && name.length >= 2) {
                pendingItemName = name
                pendingItemQty = qty
            }
        }
    }

    // Calculate subtotal if not found
    if (subtotal == 0 && items.isNotEmpty()) {
        subtotal = items.sumOf { it.totalPrice }
    }

    // Calculate total if not found
    if (total == 0) {
        total = subtotal + pajak + servis - diskon + lainnya
    }

    return ParsedReceipt(
        name = receiptName.ifEmpty { "Struk Tanpa Nama" },
        items = items,
        summary = ReceiptSummary(
            subtotal = subtotal,
            pajak = pajak,
            diskon = diskon,
            servis = servis,
            lainnya = lainnya,
            total = total
        )
    )
}
