package com.workmanager.flutter_workmanager_notification

import android.app.*
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi

class ForegroundService:Service() {
    private val channelId = "wordlang_ForegroundService_channelId"
    private val channelName = "wordlang_ForegroundService_channelName"
    private val channelDesc = "wordlang_ForegroundService_channelDesc"

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initForegroundService(intent)
        return START_STICKY
    }

    private fun initForegroundService(intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val serviceId = 256
        val notification = createNotification(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                serviceId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            )
        } else {
            startForeground(serviceId, notification)
        }
    }

    private fun createNotification(intent: Intent?): Notification {
        val id = intent?.getIntExtra("id", 0)?:0
        val title = intent?.getStringExtra("title")?:""
        val desc = intent?.getStringExtra("desc")?:""
        val btn = intent?.getStringExtra("btn")?:""
        val intent = getLaunchIntent()?.apply {
            action="click_wordland_foreground_notification"
            putExtra("id",id)
        }
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(mApplicationContext, 10001, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getActivity(mApplicationContext, 10001, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val notificationLayout = RemoteViews(mApplicationContext.packageName, R.layout.layout_notification_service)
        notificationLayout.setTextViewText(R.id.tv_title,title)
        notificationLayout.setTextViewText(R.id.tv_desc,desc)
        notificationLayout.setTextViewText(R.id.tv_btn,btn)

        val builder = Notification.Builder(this, channelId)
        builder.setOngoing(true)
        builder.setShowWhen(false)
        builder.setCustomContentView(notificationLayout)
        builder.setCustomHeadsUpContentView(notificationLayout)
        builder.setCustomBigContentView(notificationLayout)
        builder.setSmallIcon(R.drawable.logo)
        builder.setContentIntent(pendingIntent)
        builder.setContentTitle(title)
        builder.setContentText(desc)
        builder.style = Notification.BigTextStyle()
        builder.setVisibility(Notification.VISIBILITY_PUBLIC)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val nm = getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply {
                if (channelDesc != null) {
                    description = channelDesc
                }
                enableVibration(false)
            }
            nm.createNotificationChannel(channel)
        }
    }

    private fun getLaunchIntent(): Intent? {
        val packageName = mApplicationContext.packageName
        val packageManager = mApplicationContext.packageManager
        return packageManager.getLaunchIntentForPackage(packageName)
    }
}