package zuper.dev.android.dashboard.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import zuper.dev.android.dashboard.data.model.DataState
import zuper.dev.android.dashboard.data.model.FilteredInvoice
import zuper.dev.android.dashboard.data.model.FilteredJobs
import zuper.dev.android.dashboard.data.model.InvoiceApiModel
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.remote.ApiDataSource
import zuper.dev.android.dashboard.data.repo.DataRepository
import zuper.dev.android.dashboard.widjets.Utils

class DataViewModel : ViewModel() {
    private val dataRepository: DataRepository = DataRepository(ApiDataSource())

    private val _state: MutableStateFlow<DataState> =
        MutableStateFlow(DataState())
    var state = _state.asStateFlow()
        private set

    val categorizedJobs: Flow<FilteredJobs> =
        dataRepository.observeJobs()
            .map { jobsList ->
                filterJobs(jobsList)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = FilteredJobs()
            )

    private fun filterJobs(jobsList: List<JobApiModel>): FilteredJobs {
        val filteredJobs = FilteredJobs(jobs = jobsList, total = jobsList.size)

        val groupedList = jobsList.groupBy { it.status }

        groupedList.forEach {
            filteredJobs.category[it.key] = it.value
            filteredJobs.portions.add(Pair(Utils().colors[it.key], it.value.size))
        }

        return filteredJobs
    }

    val categorizedInvoice: Flow<FilteredInvoice> =
        dataRepository.observeInvoices()
            .map { invoiceList ->
                filterInvoices(invoiceList)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = FilteredInvoice()
            )

    private fun filterInvoices(invoicesList: List<InvoiceApiModel>): FilteredInvoice {
        val filteredInvoices = FilteredInvoice(invoices = invoicesList)
        val groupedInvoices = invoicesList.groupBy { it.status }

        val totalsMap = groupedInvoices.mapValues { (_, invoices) ->
            invoices.sumOf { it.total }
        }

        totalsMap.forEach {
            filteredInvoices.apply {
                total += it.value
                category[it.key] = it.value
                portions.add(Pair(Utils().colors[it.key], it.value))
            }
        }
        return filteredInvoices
    }

    fun getJobs(): FilteredJobs {
        _state.value = DataState(isLoading = true)
        val result = filterJobs(dataRepository.getJobs())
        _state.value = DataState(isLoading = false, jobs = result)
        return result
    }
}