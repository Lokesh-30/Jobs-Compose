package zuper.dev.android.dashboard.data.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import zuper.dev.android.dashboard.data.model.FilteredInvoice
import zuper.dev.android.dashboard.data.model.FilteredJobs
import zuper.dev.android.dashboard.data.model.InvoiceApiModel
import zuper.dev.android.dashboard.data.model.InvoiceStatus
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.data.remote.ApiDataSource
import zuper.dev.android.dashboard.data.repo.DataRepository
import zuper.dev.android.dashboard.widjets.Utils

class DataViewModel : ViewModel() {
    private val dataRepository: DataRepository = DataRepository(ApiDataSource())

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _jobs = mutableStateOf(FilteredJobs())
    val jobs: State<FilteredJobs> = _jobs

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
        val filteredJobs = FilteredJobs(jobs = jobsList)
        val completedList: MutableList<JobApiModel> = mutableListOf()
        val inCompletedList: MutableList<JobApiModel> = mutableListOf()
        val yetToStartList: MutableList<JobApiModel> = mutableListOf()
        val inProgressList: MutableList<JobApiModel> = mutableListOf()
        val canceledList: MutableList<JobApiModel> = mutableListOf()

        jobsList.forEach { job ->
            when (job.status) {
                JobStatus.Completed -> completedList.add(job)
                JobStatus.Incomplete -> inCompletedList.add(job)
                JobStatus.YetToStart -> yetToStartList.add(job)
                JobStatus.InProgress -> inProgressList.add(job)
                JobStatus.Cancelled -> canceledList.add(job)
            }
        }
        val map = mapOf(
            Pair(JobStatus.Completed, completedList),
            Pair(JobStatus.Incomplete, inCompletedList),
            Pair(JobStatus.YetToStart, yetToStartList),
            Pair(JobStatus.InProgress, inProgressList),
            Pair(JobStatus.Cancelled, canceledList)
        )

        filteredJobs.total = jobsList.size

        map.forEach {
            filteredJobs.category[it.key] = it.value
            filteredJobs.portions.add(Pair(Utils().colors[it.key], it.value.size))
        }

        _isLoading.value = false
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
        var draftList = 0
        var pendingList = 0
        var paidList = 0
        var badDeptList = 0

        invoicesList.forEach { invoice ->
            filteredInvoices.total += invoice.total
            when (invoice.status) {
                InvoiceStatus.Draft -> draftList += invoice.total
                InvoiceStatus.Pending -> pendingList += invoice.total
                InvoiceStatus.Paid -> paidList += invoice.total
                InvoiceStatus.BadDebt -> badDeptList += invoice.total
            }
        }
        val map = mapOf(
            Pair(InvoiceStatus.Draft, draftList),
            Pair(InvoiceStatus.Pending, pendingList),
            Pair(InvoiceStatus.Paid, paidList),
            Pair(InvoiceStatus.BadDebt, badDeptList),
        )

        map.forEach {
            filteredInvoices.category[it.key] = it.value
            filteredInvoices.portions.add(Pair(Utils().colors[it.key], it.value))
        }
        return filteredInvoices
    }

    fun getJobs(): FilteredJobs {
        _isLoading.value = true
        val result = filterJobs(dataRepository.getJobs())
        _jobs.value = result
        return result
    }
}