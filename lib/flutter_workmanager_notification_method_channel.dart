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
  Future<void> startWorkManager(int id,String title, String desc, String btn,bool test, String tbaUrl, Map<String,dynamic> tbaHeader, Map<String,dynamic> tbaParams,) async{
    await methodChannel.invokeMethod("startWorkManager",{"id":id,"title":title,"desc":desc,"btn":btn,"test":test,"tbaUrl":tbaUrl,"tbaHeader":tbaHeader,"tbaParams":tbaParams});
  }

  @override
  Future<int?> getAppLaunchNotificationId() async{
    return await methodChannel.invokeMethod("getAppLaunchNotificationId");
  }

  @override
  Future<bool> startForegroundService(int id,String title, String desc,String btn) async{
    return await methodChannel.invokeMethod("startForegroundService",{"id":id,"title":title,"desc":desc,"btn":btn});
  }

  @override
  Future<bool> requestNotificationPermission() async{
    return await methodChannel.invokeMethod("requestNotificationPermission");
  }

  @override
  Future<bool> checkNotificationPermission() async{
    return await methodChannel.invokeMethod("checkNotificationPermission");
  }

  @override
  Future<void> startBPackageWorkManager(
      int id,
      String contentListStr,
      String notificationConfStr,
      String btn,
      bool test,
      String tbaUrl,
      Map<String,dynamic> tbaHeader,
      Map<String,dynamic> tbaParams,
      ) async{
    await methodChannel.invokeMethod("startBPackageWorkManager",{"id":id,"contentListStr":contentListStr,"notificationConfStr":notificationConfStr,"btn":btn,"test":test,"tbaUrl":tbaUrl,"tbaHeader":tbaHeader,"tbaParams":tbaParams});
  }
}
