package com.plart.newsnow.core.utils.extentions

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.plart.newsnow.R

fun createNotificationChannel(
    context: Context, importance: Int, showBadge: Boolean, name: String, description: String
): String {
    val channelId = "${context.packageName}-$name"
    if (Build.VERSION.SDK_INT >= 26) {
        val channel = NotificationChannel(channelId, name, importance)
        channel.apply {
            this.description = description
            setShowBadge(showBadge)
        }
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)
    }
    return channelId
}

// Create the persistent notification//
fun getNotificationBuilder(
    context: Context,
    channelId: String,
    contentTitle: String = context.getString(R.string.tracking_location_notification_title),
    contentText: String = context.getString(R.string.tracking_location_notification_text),
    smallIcon: Int = R.drawable.ic_location
) = NotificationCompat.Builder(context, channelId).apply {
    setContentTitle(contentTitle)
    setContentText(contentText)
    //Make this notification ongoing so it can’t be dismissed by the user//
    setOngoing(true)
    setSmallIcon(smallIcon)
    priority = NotificationCompat.PRIORITY_DEFAULT
    setAutoCancel(true)
}