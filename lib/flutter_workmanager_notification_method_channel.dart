import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_workmanager_notification/notification_observer.dart';

import 'flutter_workmanager_notification_platform_interface.dart';

/// An implementation of [FlutterWorkmanagerNotificationPlatform] that uses method channels.
class MethodChannelFlutterWorkmanagerNotification extends FlutterWorkmanagerNotificationPlatform {
  NotificationObserver? _notificationObserver;
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_workmanager_notification');

  MethodChannelFlutterWorkmanagerNotification(){
    methodChannel.setMethodCallHandler((call)async{
      if(call.method=="result"){
        _notificationObserver?.clickNotification.call(call.arguments["id"]);
      }
    });
  }

  @override
  Future<void> setAppStateObserver(NotificationObserver observer)async{
    _notificationObserver=observer;
  }

  @override
  Future<void> startWorkManager(int id,String title, String desc, String btn,bool test) async{
    await methodChannel.invokeMethod("startWorkManager",{"id":id,"title":title,"desc":desc,"btn":btn,"test":test});
  }

  @override
  Future<int?> getAppLaunchNotificationId() async{
    return await methodChannel.invokeMethod("getAppLaunchNotificationId");
  }

  @override
  Future<void> startForegroundService(int id,String title, String desc,) async{
    return await methodChannel.invokeMethod("startForegroundService",{"id":id,"title":title,"desc":desc});
  }

  @override
  Future<void> requestNotificationPermission() async{
    return await methodChannel.invokeMethod("requestNotificationPermission");
  }
}
