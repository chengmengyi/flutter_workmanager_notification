package com.workmanager.flutter_workmanager_notification

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.*


class FirstInstallWorkManager(
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
      runCatching {
          val jsonObject = getJsonByStr(tbaParams)
          val json = jsonObject.getJSONObject("largesse")
          json.put("aida", UUID.randomUUID().toString())
          val client = OkHttpClient()
          val bodyBuilder = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonObject.toString())
          val builder = Request.Builder()
              .url(tbaUrl)
              .post(bodyBuilder)
              .build()
          client.newCall(builder).enqueue(object :Callback{
              override fun onFailure(call: Call, e: IOException) {
//                  Log.e("qwer","kkk==onFailure=${e.message}")
              }

              override fun onResponse(call: Call, response: Response) {
//                  Log.e("qwer","kkk==onResponse=${response.body?.string()}")
              }
          })
      }
    }

    private fun getJsonByStr(str:String):JSONObject{
        runCatching {
            return JSONObject(str)
        }
        return JSONObject()
    }
}