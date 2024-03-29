package zuper.dev.android.dashboard.data.model

import androidx.compose.ui.graphics.Color

/**
 * A simple API model representing a Job
 *
 * Start and end date time is in ISO 8601 format - Date and time are stored in UTC timezone and
 * expected to be shown on the UI in the local timezone
 */
data class JobApiModel(
    val jobNumber: Int,
    val title: String,
    val startTime: String,
    val endTime: String,
    val status: JobStatus
)

data class FilteredJobs(
    val jobs: List<JobApiModel> = emptyList(),
    var total: Int = 0,
    val portions: MutableList<Pair<Color?, Int>> = mutableListOf(),
    val category: MutableMap<JobStatus, List<JobApiModel>> = mutableMapOf()
)

enum class JobStatus {
    YetToStart,
    InProgress,
    Cancelled,
    Completed,
    Incomplete
}
