package com.samadrita.timely.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.samadrita.timely.data.ReminderDatabase

/**
 * Android clears all AlarmManager alarms on reboot, so every pending,
 * non-completed reminder must be rescheduled. Without this, a reminder
 * set for "tomorrow 7 AM" would silently vanish if the phone restarted overnight.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val dao = ReminderDatabase.getDatabase(context).reminderDao()
            CoroutineScope(Dispatchers.IO).launch {
                val pending = dao.getAllPendingReminders()
                pending.forEach { reminder ->
                    if (reminder.timeInMillis > System.currentTimeMillis()) {
                        AlarmScheduler.schedule(context, reminder)
                    }
                }
            }
        }
    }
}
