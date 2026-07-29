package com.samadrita.timely.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.samadrita.timely.notification.NotificationHelper

/**
 * Receives the alarm broadcast at the exact scheduled time and posts
 * the notification. This is the moment Timely delivers on its promise.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("extra_reminder_id", 0)
        val title = intent.getStringExtra("extra_reminder_title") ?: "Reminder"
        val note = intent.getStringExtra("extra_reminder_note") ?: ""

        NotificationHelper.showReminderNotification(context, id, title, note)
    }
}
