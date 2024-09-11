import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter_workmanager_notification/flutter_workmanager_notification.dart';
import 'package:flutter_workmanager_notification/notification_observer.dart';
import 'package:permission_handler/permission_handler.dart';
void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextButton(
                onPressed: (){
                  FlutterWorkmanagerNotification.instance.startWorkManager(id: 1000,title: "title", desc: "desc", btn: "btn");
                },
                child: Text("开始"),
              ),
              TextButton(
                onPressed: (){
                  FlutterWorkmanagerNotification.instance.setCallObserver(
                    NotificationObserver(clickNotification: (id){
                      print("kk====${id}");
                    },)
                  );
                },
                child: Text("监听"),
              ),
              TextButton(
                onPressed: (){
                  FlutterWorkmanagerNotification.instance.startForegroundService(id: 100001,title: "hahhaha", desc: "desc");
                },
                child: Text("前台服务"),
              ),
              TextButton(
                onPressed: ()async{
                  var result = await FlutterWorkmanagerNotification.instance.requestNotificationPermission();
                  print("kk====${result}");
                },
                child: Text("获取权限"),
              ),
            ],
          ),
        ),
      ),
    );
  }


  Future<bool> requestPermission({required List<Permission> permissionList})async{
    if(permissionList.isEmpty){
      return false;
    }
    Map<Permission, PermissionStatus> statuses = await permissionList.request();
    var hasPermission=true;
    var alwaysRefusePermission=false;
    statuses.forEach((key, value) {
      if(value.isPermanentlyDenied){
        alwaysRefusePermission=true;
      }
      if(!value.isGranted){
        hasPermission=false;
      }
    });
    if(alwaysRefusePermission||!hasPermission){
      // showToast("Request Permission Fail");
      return false;
    }
    return hasPermission;
  }

}
