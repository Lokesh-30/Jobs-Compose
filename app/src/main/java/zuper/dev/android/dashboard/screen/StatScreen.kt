package zuper.dev.android.dashboard.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import zuper.dev.android.dashboard.R
import zuper.dev.android.dashboard.data.model.FilteredJobs
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.data.viewmodel.DataViewModel
import zuper.dev.android.dashboard.ui.theme.Typography
import zuper.dev.android.dashboard.widjets.ChartView
import zuper.dev.android.dashboard.widjets.MyCard
import zuper.dev.android.dashboard.widjets.Utils

private val utils = Utils()

/**
 * Stats Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatScreen(navController: NavController, count: String?) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jobs ($count)",
                        style = Typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        val dataViewModel: DataViewModel = viewModel()
        dataViewModel.getJobs()
        StatContent(innerPadding, dataViewModel)
    }
}

/**
 * Stats screen main body content
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StatContent(innerPadding: PaddingValues, dataViewModel: DataViewModel) {
    val isLoading by dataViewModel.isLoading
    val jobsStat by dataViewModel.jobs
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = dataViewModel::getJobs
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background((Color.Black.copy(alpha = 0.03F)))
            .pullRefresh(pullRefreshState)
            .verticalScroll(rememberScrollState())
    ) {
        Column {
            Divider(thickness = 1.dp)
            ChartView(
                modifier = Modifier
                    .padding(20.dp),
                jobStat = jobsStat
            )
            Divider(thickness = 0.5.dp)
            TabView(jobsStat)
        }
        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * Tabs view
 */
@Composable
fun TabView(jobsStat: FilteredJobs) {
    val tabData = JobStatus.values()
    var tabIndex by remember { mutableIntStateOf(0) }
    ScrollableTabRow(
        selectedTabIndex = tabIndex,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent),
        edgePadding = 8.dp,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                color = Color.Blue,
                modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex])
            )
        },
        tabs = {
            tabData.forEachIndexed { index, s ->
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        tabIndex = (index)
                    },
                    text = {
                        Text(
                            text = stringResource(
                                R.string.tab_header,
                                s.name,
                                jobsStat.category[s]?.size ?: 0
                            ),
                            style = Typography.titleMedium
                        )
                    }
                )
            }
        }
    )
    when (tabIndex) {
        0 -> JobsListView(jobsStat, tabData[0])

        1 -> JobsListView(jobsStat, tabData[1])

        2 -> JobsListView(jobsStat, tabData[2])

        3 -> JobsListView(jobsStat, tabData[3])

        4 -> JobsListView(jobsStat, tabData[4])
    }
}

/**
 * Lists in Pager section
 */
@Composable
private fun JobsListView(
    jobsStat: FilteredJobs,
    jobStatus: JobStatus
) {
    Column(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 15.dp)) {
        jobsStat.category[jobStatus]?.forEach { job ->
            JobItem(job)
        }
    }
}

/**
 * Job individual Item
 */
@Composable
fun JobItem(job: JobApiModel) {
    MyCard(
        modifier = Modifier.padding(15.dp, 15.dp, 15.dp, 0.dp)
    ) {
        Column(
            Modifier.padding(12.dp)
        ) {
            val date = utils.formatDate(job.startTime, job.endTime)
            Text(
                text = stringResource(R.string.job_number, job.jobNumber),
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 4.dp),
                style = Typography.bodyMedium
            )
            Text(
                text = job.title,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 4.dp),
                style = Typography.titleMedium
            )
            Text(text = date, style = Typography.bodyMedium)
        }
    }
}