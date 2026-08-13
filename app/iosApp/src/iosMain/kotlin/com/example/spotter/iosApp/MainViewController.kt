package com.example.spotter.iosApp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.spotter.iosApp.di.initAppKoin
import com.example.spotter.shared.SpotterApp

fun MainViewController() = ComposeUIViewController {
    initAppKoin()
    SpotterApp()
}
