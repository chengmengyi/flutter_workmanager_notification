
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
  }){
    if(Platform.isIOS){
      return;
    }
    FlutterWorkmanagerNotificationPlatform.instance.startWorkManager(id,title,desc,btn,kDebugMode);
  }

  Future<void> setCallObserver(NotificationObserver notificationObserver)async{
    await FlutterWorkmanagerNotificationPlatform.instance.setAppStateObserver(notificationObserver);
  }

  Future<int?> getAppLaunchNotificationId()async{
    return FlutterWorkmanagerNotificationPlatform.instance.getAppLaunchNotificationId();
  }

  Future<void> startForegroundService({
    required int id,
    required String title,
    required String desc,
  })async{
    await FlutterWorkmanagerNotificationPlatform.instance.startForegroundService(id,title,desc);
  }
}
