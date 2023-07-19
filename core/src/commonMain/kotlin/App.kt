import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitoksmile.kmm.health.HealthManagerFactory

@Composable
fun App() {
    val healthManager = remember {
        HealthManagerFactory().createManager()
    }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Hello, this is HealthKMM for ${getPlatformName()}")

            healthManager.isAvailable()
                .onSuccess { isAvailable ->
                    Text("HealthManager isAvailable=$isAvailable")
                }
                .onFailure {
                    Text("HealthManager error=${it.message}")
                }
        }
    }
}

expect fun getPlatformName(): String