package com.udacity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

private const val notification_id = 1
private const val request_code = 2

val Context.notificationManager
    get() = ContextCompat.getSystemService(
        this,
        NotificationManager::class.java
    ) as NotificationManager

fun Context.sendNotification(statues: DownloadStat, filename: String) {
    val contentPendingIntent = generateContentIntent(statues, filename)

    val builder = NotificationCompat.Builder(
        this, getString(R.string.notification_channel_id)
    ).apply {
        setContentTitle(getString(R.string.notification_title))
        setContentText(getString(R.string.notification_description))
        setContentIntent(contentPendingIntent)
        setAutoCancel(true)
        setSmallIcon(R.drawable.ic_assistant_black_24dp)
        addAction(
            R.drawable.ic_baseline_more_24,
            getString(R.string.notification_button),
            contentPendingIntent
        )
    }

    createChannel()
    notificationManager.notify(notification_id, builder.build())
}

private fun Context.createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        with(channel) {
            enableLights(true)
            enableVibration(true)
            description = "Notify about completed downloads"
        }

        notificationManager.createNotificationChannel(channel)
    }
}

private fun Context.generateContentIntent(statues: DownloadStat, filename: String) =
    PendingIntent.getActivity(
        this,
        request_code,
        Intent(this, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtras(DetailActivity.generateBundle(statues, filename))
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )