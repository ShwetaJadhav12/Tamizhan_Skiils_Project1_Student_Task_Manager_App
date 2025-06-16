package com.example.tamizhan_skiils_project1_student_task_manager_app.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

@SuppressLint("ServiceCast")
fun scheduleTaskReminder(
    context: Context,
    taskTitle: String,
    taskDescription: String,
    timeInMillis: Long
) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("title", taskTitle)
        putExtra("description", taskDescription)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        taskTitle.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timeInMillis,
        pendingIntent
    )
}
