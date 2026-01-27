package com.bagi_bill.bagi_bill.presentation.camera

/**
 * Bridge between UI (commonMain) and native camera implementation.
 * Callbacks are filled by platform-specific implementations.
 */
class CameraController {
    
    /** Trigger photo capture - filled by platform implementation */
    var triggerCapture: (() -> Unit)? = null
    
    /** Toggle flash on/off - filled by platform implementation */
    var toggleFlash: (() -> Unit)? = null
    
    /** Called by UI when user taps capture button */
    fun capture() {
        triggerCapture?.invoke()
    }
    
    /** Called by UI when user taps flash button */
    fun switchFlash() {
        toggleFlash?.invoke()
    }
}
