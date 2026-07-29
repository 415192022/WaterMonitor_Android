package com.lmw.watermonitorandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lmw.watermonitorandroid.platform.compose.theme.WaterMonitorTheme
import com.lmw.watermonitorandroid.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaterMonitorTheme {
                MainScreen()
            }
        }
    }
}