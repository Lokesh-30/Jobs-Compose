package zuper.dev.android.dashboard.widjets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import zuper.dev.android.dashboard.R
import zuper.dev.android.dashboard.data.model.FilteredInvoice
import zuper.dev.android.dashboard.data.model.FilteredJobs
import zuper.dev.android.dashboard.data.model.InvoiceStatus
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.presentation.ui.theme.AppTheme
import zuper.dev.android.dashboard.presentation.ui.theme.Typography

private val utils = Utils()

/**
 * App Background
 */
@Composable
fun ZuperBackground(content: @Composable () -> Unit) {
    AppTheme {
        content()
    }
}

/**
 * Common Card view
 */
@Composable
fun MyCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        border = BorderStroke(0.5.dp, color = Color.LightGray),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        content()
    }
}

/**
 * Chart common section
 */
@Composable
fun ChartView(modifier: Modifier = Modifier, jobStat: FilteredJobs) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.jobs_count, jobStat.jobs.size), style = Typography.bodyMedium)
            Text(
                text = stringResource(
                    R.string.jobs_completed,
                    jobStat.category[JobStatus.Completed]?.size ?: 0,
                    jobStat.jobs.size
                ),
                style = Typography.bodyMedium
            )
        }
        BarChart(
            total = jobStat.total,
            portions = jobStat.portions
        )
    }
}

/**
 * Chart View
 */
@Composable
fun BarChart(
    total: Int,
    portions: MutableList<Pair<Color?, Int>>
) {
    Canvas(
        modifier = Modifier
            .padding(horizontal = 0.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(30.dp)
            .clip(RoundedCornerShape(6.dp))
    ) {
        val barWidth = size.width / total
        var currentWidth = 0f
        val sortedJobsList = portions.sortedByDescending { it.second }

        sortedJobsList.forEach { (color, value) ->
            val rectWidth = value * barWidth
            drawRect(
                color = color ?: Color.Gray,
                topLeft = Offset(currentWidth, 0f),
                size = Size(rectWidth, size.height)
            )
            currentWidth += rectWidth
        }
    }
}

/**
 * Common Chart items view
 */
@Composable
fun ChartRow(item1: JobStatus, item2: JobStatus, jobStat: FilteredJobs) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.Absolute.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChartText(item1.name, utils.colors[item1], jobStat.category[item1]?.size.toString())
        Spacer(modifier = Modifier.width(20.dp))
        ChartText(item2.name, utils.colors[item2], jobStat.category[item2]?.size.toString())
    }
}

/**
 * Common chart items view
 */
@Composable
fun ChartRow(item1: InvoiceStatus, item2: InvoiceStatus, invoiceStat: FilteredInvoice) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.Absolute.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChartText(item1.name, utils.colors[item1], "$${invoiceStat.category[item1]}")
        Spacer(modifier = Modifier.width(20.dp))
        ChartText(item2.name, utils.colors[item2], "$${invoiceStat.category[item2]}")
    }
}

/**
 * Chart item text
 */
@Composable
fun ChartText(title: String, color: Color?, count: String? = "0") {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(10.dp)
                .background(color ?: Color.Gray)
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = stringResource(R.string.item_count, title, count ?: ""), style = Typography.bodyMedium)
    }
}