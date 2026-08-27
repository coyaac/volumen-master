package com.volumelock.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.volumelock.service.VolumeForegroundService
import com.volumelock.ui.theme.VolumeLockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Asegura el servicio de monitoreo/protección en marcha (Fase 4: lo controlará el toggle).
        VolumeForegroundService.start(this)
        setContent {
            VolumeLockTheme {
                VolumeLockApp()
            }
        }
    }
}
