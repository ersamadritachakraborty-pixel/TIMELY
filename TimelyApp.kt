package com.samadrita.timely

import android.app.Application
import com.samadrita.timely.notification.NotificationHelper

class TimelyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
