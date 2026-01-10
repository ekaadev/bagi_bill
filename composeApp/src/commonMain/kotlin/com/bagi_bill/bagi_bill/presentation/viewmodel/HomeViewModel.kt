package com.bagi_bill.bagi_bill.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagi_bill.bagi_bill.data.repository.BillRepository
import com.bagi_bill.bagi_bill.domain.model.Bill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val draftBills: List<Bill> = emptyList(),
    val recentBills: List<Bill> = emptyList(),
    val draftCount: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val billRepository: BillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Load draft count
                val draftCount = billRepository.countDraftBills()

                // Load recent bills (non-draft)
                billRepository.getRecentBills(3).collect { bills ->
                    _uiState.value = _uiState.value.copy(
                        recentBills = bills,
                        draftCount = draftCount,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

    fun loadDraftBills() {
        viewModelScope.launch {
            try {
                billRepository.getDraftBills().collect { bills ->
                    _uiState.value = _uiState.value.copy(draftBills = bills)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}

