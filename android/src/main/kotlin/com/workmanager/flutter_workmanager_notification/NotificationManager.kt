package com.workmanager.flutter_workmanager_notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationManager {

    fun createTaskNotification(id:Int,title:String,desc:String,btn:String){
        val notificationLayout = RemoteViews(mApplicationContext.packageName, R.layout.layout_notification)
        notificationLayout.setTextViewText(R.id.tv_title,title)
        notificationLayout.setTextViewText(R.id.tv_desc,desc)
        notificationLayout.setTextViewText(R.id.tv_btn,btn)
        createNotification(id,notificationLayout)
    }

    @SuppressLint("MissingPermission")
    private fun createNotification(id: Int,layout: RemoteViews){
        val channelId = createNotificationChannel(
            "wordland_channel_$id",
            "wordland_channel_$id",
            NotificationManager.IMPORTANCE_MAX
        )
//        val intent = Intent(quizApp, HomeAc::class.java).apply {
//            putExtra("intent_random",intentRandom)
//            putExtra("eventName",eventName)
////            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        }
//        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.getActivity(quizApp, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//        }else{
//            PendingIntent.getActivity(quizApp, id, intent, PendingIntent.FLAG_ONE_SHOT)
//        }

        val intent = getLaunchIntent()?.apply {
            action="click_wordland_notification"
            putExtra("id",id)
        }
//        var flags = PendingIntent.FLAG_UPDATE_CURRENT
//        if (VERSION.SDK_INT >= VERSION_CODES.M) {
//            flags = flags or PendingIntent.FLAG_IMMUTABLE
//        }

        val pendingIntent = if (VERSION.SDK_INT >= VERSION_CODES.S) {
            PendingIntent.getActivity(mApplicationContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getActivity(mApplicationContext, id, intent, PendingIntent.FLAG_ONE_SHOT)
        }

//        val pendingIntent = PendingIntent.getActivity(mApplicationContext, id, intent, flags)

        val notification = NotificationCompat.Builder(mApplicationContext, channelId?:"my_channel_$id")
            .setSmallIcon(R.drawable.logo)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(layout)
            .setCustomHeadsUpContentView(layout)
//            .setCustomBigContentView(largeViews)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat.from(mApplicationContext)
        notificationManager.notify(id, notification.build())
    }

    private fun createNotificationChannel(channelID: String, channelNAME: String, level: Int): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            val channel = NotificationChannel(channelID, channelNAME, level)
            manager!!.createNotificationChannel(channel)
            channelID
        } else {
            null
        }
    }

    private fun getLaunchIntent(): Intent? {
        val packageName = mApplicationContext.packageName
        val packageManager = mApplicationContext.packageManager
        return packageManager.getLaunchIntentForPackage(packageName)
    }
}