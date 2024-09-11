package com.workmanager.flutter_workmanager_notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorkManager(
    context: Context,
    workerParams: WorkerParameters,
) :Worker(context, workerParams) {
    override fun doWork(): Result {
        val id = inputData.getInt("id",0)
        val title = inputData.getString("title")?:"Check your account"
        val desc = inputData.getString("desc")?:"Complete tasks to earning."
        val btn = inputData.getString("btn")?:"Check"
        NotificationManager.createTaskNotification(id,title, desc, btn)
        return Result.success()
    }

}