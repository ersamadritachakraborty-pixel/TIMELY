package com.samadrita.timely.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.samadrita.timely.data.Reminder

/**
 * Wraps AlarmManager so reminders fire at the *exact* moment they are due.
 * This is the core promise of Timely: "right before it's too late", never late.
 */
object AlarmScheduler {

    private const val EXTRA_ID = "extra_reminder_id"
    private const val EXTRA_TITLE = "extra_reminder_title"
    private const val EXTRA_NOTE = "extra_reminder_note"

    fun schedule(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_ID, reminder.id)
            putExtra(EXTRA_TITLE, reminder.title)
            putExtra(EXTRA_NOTE, reminder.note)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, reminder.timeInMillis, pendingIntent
                )
            } else {
                // Fallback: inexact alarm if the exact-alarm permission was denied.
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, reminder.timeInMillis, pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, reminder.timeInMillis, pendingIntent
            )
        }
    }

    fun cancel(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
