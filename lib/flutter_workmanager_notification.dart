
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter_workmanager_notification/notification_observer.dart';

import 'flutter_workmanager_notification_platform_interface.dart';

class FlutterWorkmanagerNotification {
  static final FlutterWorkmanagerNotification _instance = FlutterWorkmanagerNotification();

  static FlutterWorkmanagerNotification get instance => _instance;

  startWorkManager({
    required int id,
    required String title,
    required String desc,
    required String btn,
    required String tbaUrl,
    required Map<String,dynamic> tbaHeader,
    required Map<String,dynamic> tbaParams,
  }){
    if(Platform.isIOS){
      return;
    }
    FlutterWorkmanagerNotificationPlatform.instance.startWorkManager(id,title,desc,btn,kDebugMode,tbaUrl,tbaHeader,tbaParams);
  }

  Future<void> setCallObserver(NotificationObserver notificationObserver)async{
    await FlutterWorkmanagerNotificationPlatform.instance.setAppStateObserver(notificationObserver);
  }

  Future<int?> getAppLaunchNotificationId()async{
    return FlutterWorkmanagerNotificationPlatform.instance.getAppLaunchNotificationId();
  }

  Future<bool> startForegroundService({
    required int id,
    required String title,
    required String desc,
    required String btn,
  })async{
    return await FlutterWorkmanagerNotificationPlatform.instance.startForegroundService(id,title,desc,btn);
  }

  Future<bool> requestNotificationPermission()async{
    return await FlutterWorkmanagerNotificationPlatform.instance.requestNotificationPermission();
  }

  Future<bool> checkNotificationPermission()async{
    return await FlutterWorkmanagerNotificationPlatform.instance.checkNotificationPermission();
  }

  startBPackageWorkManager({
    required int id,
    required String contentListStr,
    required String notificationConfStr,
    required String btn,
    required String tbaUrl,
    required Map<String,dynamic> tbaHeader,
    required Map<String,dynamic> tbaParams,
  }){
    if(Platform.isIOS){
      return;
    }
    FlutterWorkmanagerNotificationPlatform.instance.startBPackageWorkManager(id,contentListStr,notificationConfStr,btn,kDebugMode,tbaUrl,tbaHeader,tbaParams);
  }
}
