package zuper.dev.android.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import zuper.dev.android.dashboard.navigation.ScreenNavigation
import zuper.dev.android.dashboard.widjets.ZuperBackground


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZuperBackground {
                ScreenNavigation()
            }
        }
    }
}

@Preview
@Composable
fun Greeting() {
    ZuperBackground {
        Text(
            text = "Hello name!"
        )
    }
}