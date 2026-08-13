package com.example.spotter

import android.app.Application
import android.content.Context
import com.example.spotter.shared.di.initAppKoin
import org.osmdroid.config.Configuration

class SpotterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initAppKoin(this)
        initOsmdroid(this)
    }

    companion object {
        fun initOsmdroid(context: Context) {
            Configuration.getInstance().apply {
                load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                userAgentValue = context.packageName
            }
        }
    }
}
