package com.samadrita.timely

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samadrita.timely.data.Priority
import com.samadrita.timely.data.Reminder
import com.samadrita.timely.databinding.ItemReminderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderAdapter(
    private val onItemClick: (Reminder) -> Unit,
    private val onCheckClick: (Reminder) -> Unit,
    private val onDeleteClick: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(DiffCallback()) {

    inner class ReminderViewHolder(val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = getItem(position)
        val format = SimpleDateFormat("EEE, dd MMM · hh:mm a", Locale.getDefault())

        with(holder.binding) {
            textTitle.text = reminder.title
            textTime.text = format.format(Date(reminder.timeInMillis))
            checkboxDone.isChecked = reminder.isCompleted

            textTitle.paintFlags = if (reminder.isCompleted) {
                textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            val priorityColor = when (reminder.priority) {
                Priority.HIGH -> R.color.priority_high
                Priority.NORMAL -> R.color.priority_normal
                Priority.LOW -> R.color.priority_low
            }
            priorityDot.setBackgroundResource(priorityColor)

            root.setOnClickListener { onItemClick(reminder) }
            checkboxDone.setOnClickListener { onCheckClick(reminder) }
            buttonDelete.setOnClickListener { onDeleteClick(reminder) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) =
            oldItem == newItem
    }
}
