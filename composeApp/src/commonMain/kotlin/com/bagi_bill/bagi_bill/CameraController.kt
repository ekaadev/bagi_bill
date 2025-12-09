package com.bagi_bill.bagi_bill

class CameraController {
    // --- KABEL PENGHUBUNG (CALLBACK) ---
    // Nanti sisi Android yang akan mengisi variabel ini dengan fungsi aslinya

    // 1. Kabel Jepret
    var triggerCapture: (() -> Unit)? = null

    // 2. Kabel Flash
    var toggleFlash: (() -> Unit)? = null

    // --- TOMBOL PEMICU (Dipanggil UI) ---

    // Saat tombol Shutter ditekan
    fun capture() {
        triggerCapture?.invoke() // Kirim sinyal ke Android
    }

    // Saat tombol Flash ditekan
    fun switchFlash() {
        toggleFlash?.invoke() // Kirim sinyal ke Android
    }
}