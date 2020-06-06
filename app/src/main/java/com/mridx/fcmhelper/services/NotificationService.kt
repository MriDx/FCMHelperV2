/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mridx.fcmhelper.R
import com.mridx.fcmhelper.ui.MainUI

class NotificationService : FirebaseMessagingService() {

    private val notificationChannelId: String = "MainNotificationChannel"
    private val pendingIntentRequest: Int = 1001

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            setupChannel(notificationManager)

        notificationManager.notify(1000000, createNotification(p0))

    }

    private fun setupChannel(notificationManager: NotificationManagerCompat) {
        val adminChannel = NotificationChannel(
            notificationChannelId,
            "MAIN_CHANNEL",
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = "This is main channel for notification"
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }

    private fun createNotification(message: RemoteMessage): Notification {
        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setAutoCancel(true)
            .setVibrate(createVibration())
            .setLights(Color.RED, 1, 1)
            .setSound(defaultSoundUri())
            .setContentIntent(createIntent(message))
        return notificationBuilder.build()
    }

    private fun createIntent(message: RemoteMessage): PendingIntent? {
        val intent: Intent = Intent(this, MainUI::class.java)
        if (message.data.containsKey("action")) {
            buildIntent(message, intent)
        }


        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return PendingIntent.getActivity(
            this,
            pendingIntentRequest,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    }

    private fun buildIntent(message: RemoteMessage, intent: Intent)  : Intent {
        if (message.data["action"].equals("unsubscribe")) {
            intent.putExtra("action", message.data["action"])
            intent.putExtra("value", message.data["value"])
        }
        return intent
    }

    private fun defaultSoundUri(): Uri? {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    private fun createVibration(): LongArray? {
        return longArrayOf(0, 1000, 0)
    }
}