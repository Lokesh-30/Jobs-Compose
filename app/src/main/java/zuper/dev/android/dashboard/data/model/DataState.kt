package zuper.dev.android.dashboard.data.model

data class DataState(
    val isLoading : Boolean = false,
    val jobs: FilteredJobs = FilteredJobs()
)