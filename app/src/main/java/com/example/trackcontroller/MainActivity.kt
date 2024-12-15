package com.example.trackcontroller

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trackcontroller.ui.theme.TrackControllerTheme

class MainActivity : ComponentActivity() {

    private var isVolumeListeningEnabled = false
    private var lastVolumeDownPressTime: Long = 0
    private val doublePressInterval = 300
    private var lastVolumeLevel = 0

    private lateinit var audioManager: AudioManager
    private lateinit var volumeObserver: ContentObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        lastVolumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        volumeObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                handleVolumeChange(true)
            }
        }
        contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            volumeObserver
        )

        setContent {
            TrackControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        isListeningEnabled = isVolumeListeningEnabled,
                        onToggleListening = { isEnabled ->
                            isVolumeListeningEnabled = isEnabled
                            Toast.makeText(
                                this@MainActivity,
                                if (isEnabled) "Listener activado" else "Listener desactivado",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun handleVolumeChange(isVolumeDown: Boolean) {
        if (!isVolumeDown) return

        val currentTime = System.currentTimeMillis()

        if (currentTime - lastVolumeDownPressTime <= doublePressInterval) {
            playNextSong()
            lastVolumeDownPressTime = 0
        } else {
            lastVolumeDownPressTime = currentTime
        }
    }



    private fun playNextSong() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT)
        audioManager.dispatchMediaKeyEvent(keyEvent)

        println("Next song command sent using AudioManager")
    }



    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(volumeObserver)
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
