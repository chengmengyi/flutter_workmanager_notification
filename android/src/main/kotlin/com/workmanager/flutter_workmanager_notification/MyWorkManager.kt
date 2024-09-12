package com.workmanager.flutter_workmanager_notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.util.*

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
        uploadTba(inputData.getString("tbaUrl")?:"",inputData.getString("tbaHeader")?:"",inputData.getString("tbaParams")?:"")
        return Result.success()
    }

    private fun uploadTba(tbaUrl:String,tbaHeader:String,tbaParams:String){
        val jsonObject = getJsonByStr(tbaParams)
        val json = jsonObject.getJSONObject("largesse")
        json.put("aida", UUID.randomUUID().toString())
    }

    private fun getJsonByStr(str:String):JSONObject{
        runCatching {
            return JSONObject(str)
        }
        return JSONObject()
    }
}