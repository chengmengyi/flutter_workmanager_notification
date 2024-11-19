package com.workmanager.flutter_workmanager_notification

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*


class MyWorkManager(
    context: Context,
    workerParams: WorkerParameters,
) :Worker(context, workerParams) {


    override fun doWork(): Result {

        val id = inputData.getInt("id",0)
        val contentListStr = inputData.getString("contentListStr")?:""
        val btn = inputData.getString("btn")?:"Check"

        val list = getNotificationContentList(contentListStr)
        val contentBean=if(list.isEmpty()){
            NotificationContentBean("Check your account","Complete tasks to earning.")
        }else{
            var index = getLocalNotificationIndex()
            if(index>=list.size){
                index=0
            }
            writeLocalNotificationIndex(index+1)
            list[index]
        }

        NotificationManager.createTaskNotification(id,contentBean.title, contentBean.content, btn)
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


    private fun getNotificationContentList(string: String?):ArrayList<NotificationContentBean>{
        val list= arrayListOf<NotificationContentBean>()
        runCatching {
            val array = JSONArray(string ?: "")
            for (i in 0 until array.length()) {
                val jsonObject = array.getJSONObject(i)
                list.add(NotificationContentBean(jsonObject.optString("title"),jsonObject.optString("content")))
            }
            return list
        }
        return  list
    }

    private fun getLocalNotificationIndex()=applicationContext.getSharedPreferences("wordland",Context.MODE_PRIVATE).getInt("index",0)

    private fun writeLocalNotificationIndex(index:Int){
        applicationContext.getSharedPreferences("wordland", Context.MODE_PRIVATE).edit().apply {
            putInt("index",index)
            apply()
        }
    }
}