package com.bagi_bill.bagi_bill.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.bagi_bill.bagi_bill.domain.parser.ParsedReceipt
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.SplitBillData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for sharing scan results between Camera, Rincian, SelectMember, and SplitBill screens.
 */
class ScanViewModel : ViewModel() {
    
    private val _parsedReceipt = MutableStateFlow<ParsedReceipt?>(null)
    val parsedReceipt: StateFlow<ParsedReceipt?> = _parsedReceipt.asStateFlow()

    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes: StateFlow<ByteArray?> = _imageBytes.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // SplitBillData from SelectMember screen
    private val _splitBillData = MutableStateFlow<SplitBillData?>(null)
    val splitBillData: StateFlow<SplitBillData?> = _splitBillData.asStateFlow()

    fun setParsedReceipt(receipt: ParsedReceipt) {
        _parsedReceipt.value = receipt
    }

    fun setImageBytes(bytes: ByteArray) {
        _imageBytes.value = bytes
    }

    fun setProcessing(processing: Boolean) {
        _isProcessing.value = processing
    }

    fun setError(message: String?) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setSplitBillData(data: SplitBillData) {
        _splitBillData.value = data
    }

    fun clearData() {
        _parsedReceipt.value = null
        _imageBytes.value = null
        _errorMessage.value = null
        _splitBillData.value = null
    }
}
