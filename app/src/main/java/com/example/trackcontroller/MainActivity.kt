package com.example.trackcontroller

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trackcontroller.service.VolumeService
import com.example.trackcontroller.ui.theme.TrackControllerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, VolumeService::class.java)

        setContent {
            TrackControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        isListeningEnabled = false,
                        onToggleListening = { isEnabled ->
                            if (isEnabled) {
                                startService(serviceIntent)
                            }
                            else {
                                stopService(serviceIntent)
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    isListeningEnabled: Boolean,
    onToggleListening: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEnabled by remember { mutableStateOf(isListeningEnabled) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Hello Android!")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            isEnabled = !isEnabled
            onToggleListening(isEnabled)
        }) {
            Text(
                text = if (isEnabled) "Desactivar Listener" else "Activar Listener"
            )
        }
    }
}
