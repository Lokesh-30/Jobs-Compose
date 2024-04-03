package zuper.dev.android.dashboard.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import zuper.dev.android.dashboard.R
import zuper.dev.android.dashboard.data.model.FilteredInvoice
import zuper.dev.android.dashboard.data.model.FilteredJobs
import zuper.dev.android.dashboard.data.model.InvoiceStatus
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.data.viewmodel.DataViewModel
import zuper.dev.android.dashboard.presentation.ui.theme.Typography
import zuper.dev.android.dashboard.widjets.AppScreens
import zuper.dev.android.dashboard.widjets.BarChart
import zuper.dev.android.dashboard.widjets.ChartRow
import zuper.dev.android.dashboard.widjets.ChartText
import zuper.dev.android.dashboard.widjets.ChartView
import zuper.dev.android.dashboard.widjets.MyCard
import zuper.dev.android.dashboard.widjets.Utils

private val utils = Utils()

/**
 * Dashboard Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard),
                        style = Typography.titleLarge
                    )
                }
            )
        },
    ) { innerPadding ->
        DashboardContent(navController, innerPadding)
    }
}

/**
 * Dashboard Screen body content
 */
@Composable
fun DashboardContent(
    navController: NavController,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.Black.copy(alpha = 0.03F)),
    ) {
        Divider(thickness = 0.5.dp)
        ProfileCard()
        Stats(navController)
    }
}

/**
 * Stats section
 */
@Composable
fun Stats(navController: NavController, dataViewModel: DataViewModel = viewModel()) {
    val jobStat by dataViewModel.categorizedJobs.collectAsState(initial = FilteredJobs())
    val invoiceStat by dataViewModel.categorizedInvoice.collectAsState(initial = FilteredInvoice())
    StatItem(
        title = stringResource(R.string.job_stats),
        modifier = Modifier
            .padding(15.dp, 10.dp, 15.dp, 0.dp)
            .clickable {
                navController.navigate(route = AppScreens.StatScreen.name + "/${jobStat.jobs.size}")
            }
    ) {
        JobStatChart(jobStat)
    }
    StatItem(
        title = stringResource(R.string.invoice_stat),
        modifier = Modifier
            .padding(15.dp, 10.dp, 15.dp, 0.dp)
    ) {
        InvoiceStatChart(invoiceStat)
    }
}

/**
 * Common View of Stats
 */
@Composable
fun StatItem(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    MyCard(
        modifier = modifier
    ) {
        Column {
            Text(
                modifier = Modifier.padding(15.dp),
                text = title,
                style = Typography.titleLarge
            )
            Divider(thickness = 0.5.dp)
            content()
        }
    }
}

/**
 * Job Stats View
 */
@Composable
private fun JobStatChart(jobStat: FilteredJobs) {
    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
    ) {
        ChartView(jobStat = jobStat)
        ChartRow(JobStatus.Completed, JobStatus.Incomplete, jobStat)
        ChartRow(JobStatus.YetToStart, JobStatus.InProgress, jobStat)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Absolute.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChartText(
                JobStatus.Cancelled.name,
                utils.colors[JobStatus.Cancelled],
                jobStat.category[JobStatus.Cancelled]?.size.toString()
            )
        }
    }
}

/**
 * Invoice Stats View
 */
@Composable
private fun InvoiceStatChart(invoiceStat: FilteredInvoice) {

    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.total_value_jobs, invoiceStat.total), style = Typography.bodyMedium)
            Text(
                text = stringResource(R.string.collected, invoiceStat.category[InvoiceStatus.Paid] ?: 0),
                style = Typography.bodyMedium
            )
        }
        BarChart(
            total = invoiceStat.total,
            portions = invoiceStat.portions
        )
        ChartRow(InvoiceStatus.Paid, InvoiceStatus.Pending, invoiceStat)
        ChartRow(InvoiceStatus.BadDebt, InvoiceStatus.Draft, invoiceStat)
    }
}

/**
 * Profile Card view
 */
@Composable
fun ProfileCard() {
    MyCard(
        modifier = Modifier
            .padding(15.dp, 10.dp, 15.dp, 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = stringResource(R.string.hello_lokesh), style = Typography.titleLarge)
                Text(text = utils.getCurrentDate(), style = Typography.bodyMedium)
            }
            Image(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp)),
                painter = painterResource(id = R.drawable.profile_image),
                contentDescription = stringResource(R.string.profile_image),
            )
        }
    }
}