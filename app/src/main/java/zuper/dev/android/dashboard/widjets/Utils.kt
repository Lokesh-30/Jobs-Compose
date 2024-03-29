package zuper.dev.android.dashboard.widjets

import androidx.compose.ui.graphics.Color
import zuper.dev.android.dashboard.data.model.InvoiceStatus
import zuper.dev.android.dashboard.data.model.JobStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {

    /**
     * colors for chart view
     */
    val colors = mapOf(
        JobStatus.YetToStart to Color.Cyan,
        JobStatus.InProgress to Color.Blue,
        JobStatus.Cancelled to Color.Yellow,
        JobStatus.Completed to Color.Green,
        JobStatus.Incomplete to Color.Red,
        InvoiceStatus.BadDebt to Color.Red,
        InvoiceStatus.Draft to Color.Yellow,
        InvoiceStatus.Paid to Color.Green,
        InvoiceStatus.Pending to Color.Blue
    )

    /**
     * Date Time Formatter
     */
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d'th' yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Date Time Formatter
     */
    fun formatDate(startDate: String, endDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault())
        val todayFormat = SimpleDateFormat("'Today', hh:mm a", Locale.getDefault())

        val startDateParsed = inputFormat.parse(startDate)
        val endDateParsed = inputFormat.parse(endDate)
        val currentDate = Date()

        return if (startDateParsed != null && endDateParsed != null) {
            val startDateFormatted = outputFormat.format(startDateParsed)
            val endDateFormatted = outputFormat.format(endDateParsed)
            val currentDateFormatted = outputFormat.format(currentDate)

            val startDay = startDateFormatted.substring(0, 10)
            val endDay = endDateFormatted.substring(0, 10)
            val currentDay = currentDateFormatted.substring(0, 10)

            val startTime = startDateFormatted.substring(12)
            val endTime = endDateFormatted.substring(12)

            when {
                startDay == endDay && startDay == currentDay -> {
                    todayFormat.format(startDateParsed) + " - " + todayFormat.format(endDateParsed)
                }
                startDay == endDay -> {
                    "$startDay, $startTime - $endTime"
                }
                endDay == currentDay -> {
                    "$startDay, $startTime - Today, $endTime"
                }
                else -> {
                    "$startDay, $startTime -> $endDay, $endTime"
                }
            }
        } else {
            ""
        }
    }

}