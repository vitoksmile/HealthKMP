import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        Text(
            "Hello, HealthKMM from ${getPlatformName()}",
            modifier = Modifier.padding(16.dp),
        )
    }
}

expect fun getPlatformName(): String