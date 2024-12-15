package com.example.trackcontroller.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Toast

class VolumeService : Service() {

    private lateinit var audioManager: AudioManager
    private lateinit var volumeObserver: ContentObserver
    private var lastVolumeDownPressTime: Long = 0
    private val doublePressInterval = 300

    override fun onCreate() {
        super.onCreate()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        volumeObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                handleVolumeChange()
            }
        }

        contentResolver.registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                volumeObserver
        )
    }

    private fun playNextSong() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT)
        audioManager.dispatchMediaKeyEvent(keyEvent)

        println("Next song command sent using AudioManager")
        Toast.makeText(this, "Next song command sent", Toast.LENGTH_SHORT).show()
    }

    private fun handleVolumeChange() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastVolumeDownPressTime <= doublePressInterval) {
            playNextSong()
            lastVolumeDownPressTime = 0
        } else {
            lastVolumeDownPressTime = currentTime
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(volumeObserver)
    }

    override fun onBind(intent: Intent?) = null
}
