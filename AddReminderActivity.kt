package com.samadrita.timely

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.samadrita.timely.data.Priority
import com.samadrita.timely.data.Reminder
import com.samadrita.timely.databinding.ActivityAddReminderBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddReminderActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
    }

    private lateinit var binding: ActivityAddReminderBinding
    private val viewModel: ReminderViewModel by lazy {
        androidx.lifecycle.ViewModelProvider(this)[ReminderViewModel::class.java]
    }

    private val calendar = Calendar.getInstance()
    private var editingReminder: Reminder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, -1)
        if (reminderId != -1) {
            supportActionBar?.title = getString(R.string.edit_reminder)
            loadReminder(reminderId)
        } else {
            supportActionBar?.title = getString(R.string.new_reminder)
            updateDateTimeLabel()
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.buttonPickDate.setOnClickListener { showDatePicker() }
        binding.buttonPickTime.setOnClickListener { showTimePicker() }

        binding.radioGroupPriority.setOnCheckedChangeListener { _, _ -> }

        binding.buttonSave.setOnClickListener { saveReminder() }
    }

    private fun loadReminder(id: Int) {
        lifecycleScope.launch {
            val reminder = viewModel.let {
                com.samadrita.timely.data.ReminderDatabase.getDatabase(applicationContext)
                    .reminderDao().getReminderById(id)
            }
            reminder?.let {
                editingReminder = it
                binding.editTitle.setText(it.title)
                binding.editNote.setText(it.note)
                calendar.timeInMillis = it.timeInMillis
                updateDateTimeLabel()
                when (it.priority) {
                    Priority.LOW -> binding.radioLow.isChecked = true
                    Priority.NORMAL -> binding.radioNormal.isChecked = true
                    Priority.HIGH -> binding.radioHigh.isChecked = true
                }
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateTimeLabel()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                updateDateTimeLabel()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun updateDateTimeLabel() {
        val format = SimpleDateFormat("EEE, dd MMM yyyy · hh:mm a", Locale.getDefault())
        binding.textSelectedDateTime.text = format.format(calendar.time)
    }

    private fun saveReminder() {
        val title = binding.editTitle.text.toString().trim()
        if (title.isEmpty()) {
            binding.editTitle.error = getString(R.string.title_required)
            return
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, R.string.pick_future_time, Toast.LENGTH_SHORT).show()
            return
        }

        val note = binding.editNote.text.toString().trim()
        val priority = when (binding.radioGroupPriority.checkedRadioButtonId) {
            R.id.radioLow -> Priority.LOW
            R.id.radioHigh -> Priority.HIGH
            else -> Priority.NORMAL
        }

        val reminder = editingReminder?.copy(
            title = title,
            note = note,
            timeInMillis = calendar.timeInMillis,
            priority = priority
        ) ?: Reminder(
            title = title,
            note = note,
            timeInMillis = calendar.timeInMillis,
            priority = priority
        )

        if (editingReminder != null) {
            viewModel.updateReminder(reminder)
        } else {
            viewModel.addReminder(reminder)
        }
        finish()
    }
}
