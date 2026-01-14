package com.bagi_bill.bagi_bill.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagi_bill.bagi_bill.data.repository.BillRepository
import com.bagi_bill.bagi_bill.domain.model.Bill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.ExperimentalTime

data class HistoryUiState(
    val bills: List<Bill> = emptyList(),
    val groupedBills: Map<LocalDate, List<Bill>> = emptyMap(),
    val selectedDateRange: DateRangeOption = DateRangeOption.LAST_7_DAYS,
    val tempSelectedDateRange: DateRangeOption? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

enum class DateRangeOption(val days: Int, val label: String) {
    LAST_7_DAYS(7, "7 hari terakhir"),
    LAST_30_DAYS(30, "30 hari terakhir"),
    LAST_90_DAYS(90, "90 hari terakhir")
}

@OptIn(ExperimentalTime::class)
class HistoryViewModel(
    private val billRepository: BillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadBills()
    }

    fun loadBills(dateRangeOption: DateRangeOption = DateRangeOption.LAST_7_DAYS) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val timeZone = TimeZone.currentSystemDefault()
                val today = kotlin.time.Clock.System.todayIn(timeZone)
                val startDate = today.minus(dateRangeOption.days - 1, DateTimeUnit.DAY)

                val startInstant = startDate.atStartOfDayIn(timeZone)
                val endInstant = today.atStartOfDayIn(timeZone)
                val endOfDayMillis = 86400000L - 1
                val endInstantWithTime = Instant.fromEpochMilliseconds(
                    endInstant.toEpochMilliseconds() + endOfDayMillis
                )

                billRepository.getBillsByDateRange(startInstant, endInstantWithTime).collect { bills ->
                    val filteredBills = bills.filter { !it.isDraft }
                    val grouped = filteredBills.groupBy { bill ->
                        bill.createdDate.toLocalDate()
                    }

                    val sortedGrouped = grouped.entries
                        .sortedByDescending { it.key }
                        .associate { it.key to it.value }

                    _uiState.value = _uiState.value.copy(
                        bills = filteredBills,
                        groupedBills = sortedGrouped,
                        selectedDateRange = dateRangeOption,
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

    fun updateDateRange(dateRangeOption: DateRangeOption) {
        loadBills(dateRangeOption)
    }

    fun setTempDateRange(dateRangeOption: DateRangeOption) {
        _uiState.value = _uiState.value.copy(tempSelectedDateRange = dateRangeOption)
    }

    fun applyFilter() {
        val tempRange = _uiState.value.tempSelectedDateRange
        if (tempRange != null) {
            loadBills(tempRange)
            _uiState.value = _uiState.value.copy(tempSelectedDateRange = null)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                val currentRange = _uiState.value.selectedDateRange
                loadBills(currentRange)
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun Instant.toLocalDate(): LocalDate {
    val epochMillis = this.toEpochMilliseconds()
    val epochSeconds = epochMillis / 1000
    val days = (epochSeconds / 86400).toInt()
    val epochDays = days

    val year: Int
    val month: Int
    val day: Int

    val remaining = epochDays + 719468
    val era = if (remaining >= 0) remaining / 146097 else (remaining - 146096) / 146097
    val dayOfEra = remaining - era * 146097
    val yearOfEra = (dayOfEra - dayOfEra / 1460 + dayOfEra / 36524 - dayOfEra / 146096) / 365
    year = yearOfEra + era * 400
    val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val mp = (5 * dayOfYear + 2) / 153
    day = dayOfYear - (153 * mp + 2) / 5 + 1
    month = mp + if (mp < 10) 3 else -9

    return LocalDate(year + if (month <= 2) 1 else 0, month, day)
}

