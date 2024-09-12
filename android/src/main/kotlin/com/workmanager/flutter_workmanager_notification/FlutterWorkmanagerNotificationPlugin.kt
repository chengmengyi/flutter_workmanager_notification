package com.workmanager.flutter_workmanager_notification

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry
import org.json.JSONObject
import java.util.concurrent.TimeUnit

lateinit var mApplicationContext:Context

class FlutterWorkmanagerNotificationPlugin: FlutterPlugin, MethodCallHandler, PluginRegistry.NewIntentListener, ActivityAware,PluginRegistry.RequestPermissionsResultListener  {
  private lateinit var channel : MethodChannel
  private var mActivity:Activity?=null
    private lateinit var result: MethodChannel.Result


  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
      channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_workmanager_notification")
      channel.setMethodCallHandler(this)
      mApplicationContext=flutterPluginBinding.applicationContext
  }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        this.result=result
        when(call.method){
            "startWorkManager"->{
                call.arguments?.let {
                    runCatching {
                        val map = it as? Map<*, *>
                        val builder = Data.Builder()
                            .putInt("id",(map?.get("id") as? Int)?:0)
                            .putString("title",(map?.get("title") as? String)?:"")
                            .putString("desc",(map?.get("desc") as? String)?:"")
                            .putString("btn",(map?.get("btn") as? String)?:"")
                            .putString("tbaUrl",(map?.get("tbaUrl") as? String)?:"")
                            .putString("tbaHeader",getStrByMap((map?.get("tbaHeader") as? Map<String, Any>)?: hashMapOf()))
                            .putString("tbaParams",getStrByMap((map?.get("tbaParams") as? Map<String, Any>)?: hashMapOf()))
                            .build()
                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                            .setRequiresBatteryNotLow(true).build()

                        if((map?.get("test") as? Boolean) == true){
                            val workRequest=OneTimeWorkRequest
                                .Builder(MyWorkManager::class.java)
                                .setConstraints(constraints)
                                .setInitialDelay(10000,TimeUnit.MILLISECONDS)
                                .setInputData(builder)
                                .build()
                            WorkManager.getInstance(mApplicationContext).enqueue(workRequest)
                        }else{
                            val periodicWorkRequest = PeriodicWorkRequest
                                .Builder(MyWorkManager::class.java, 6, TimeUnit.HOURS)
                                .setConstraints(constraints)
                                .setInputData(builder)
                                .build()
                            WorkManager.getInstance(mApplicationContext).enqueue(periodicWorkRequest)
                        }
                    }
                }
            }
            "getAppLaunchNotificationId"->{
                if(null!=mActivity){
                    val intent = mActivity?.intent
                    if(null!=intent&&("click_wordland_notification"==intent.action||"click_wordland_foreground_notification"==intent.action)&&!launchedActivityFromHistory(intent)){
                        result.success(intent.extras?.getInt("id")?:0)
                    }else{
                        result.success(null)
                    }
                }else{
                    result.success(null)
                }
            }
            "startForegroundService"->{
                val map = call.arguments as? Map<*, *>
                val nIntent = Intent(mApplicationContext, ForegroundService::class.java)
                nIntent.putExtra("id",(map?.get("id") as? Int)?:0)
                nIntent.putExtra("title",(map?.get("title") as? String)?:"")
                nIntent.putExtra("desc",(map?.get("desc") as? String)?:"")
                ContextCompat.startForegroundService(mApplicationContext, nIntent)
            }

            "checkNotificationPermission"->{
                mActivity?.let {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        result.success(true)
                    }
                    if (it.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)) {
                        result.success(true)
                    } else {
                        result.success(false)
                    }
                }
            }

            "requestNotificationPermission" -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    result.success(true)
                    return
                }
                mActivity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                      1000
                    )
                }
            }
        }
    }


    private fun Context.isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun launchedActivityFromHistory(intent: Intent?): Boolean {
        return (intent != null
                && intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
                == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)
    }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

    override fun onNewIntent(p0: Intent): Boolean {
        if("click_wordland_notification"==p0.action||"click_wordland_foreground_notification"==p0.action){
            val map = HashMap<String, Int>()
            map["id"] = p0.extras?.getInt("id")?:0
            channel.invokeMethod("result", map)
            return true
        }
        return false
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        binding.addOnNewIntentListener(this)
        binding.addRequestPermissionsResultListener(this)
        mActivity=binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        binding.addOnNewIntentListener(this)
        binding.addRequestPermissionsResultListener(this)
        mActivity=binding.activity
    }

    override fun onDetachedFromActivity() {
        mActivity=null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        if(grantResults.isEmpty()){
            result.success(false)
            return false
        }
        val permission: String
        val permissionIndex: Int
        var permissionStatus = false
        when (requestCode) {
            1000 -> {
                permission = Manifest.permission.POST_NOTIFICATIONS
                permissionIndex = permissions.indexOf(permission)
                if (permissionIndex >= 0
                    && grantResults[permissionIndex] == PackageManager.PERMISSION_GRANTED) {
                    permissionStatus = true
                } else {
                    if (mActivity?.shouldShowRequestPermissionRationale(permission) == false) {
                        permissionStatus = false
                    }
                }
                result.success(permissionStatus)
            }
            else -> return false
        }
        return true
    }

    private fun getStrByMap(map:Map<String, Any>):String{
        runCatching {
            return JSONObject(map).toString()
        }
        return ""
    }
}
