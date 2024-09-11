import 'package:flutter_workmanager_notification/notification_observer.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_workmanager_notification_method_channel.dart';

abstract class FlutterWorkmanagerNotificationPlatform extends PlatformInterface {
  /// Constructs a FlutterWorkmanagerNotificationPlatform.
  FlutterWorkmanagerNotificationPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterWorkmanagerNotificationPlatform _instance = MethodChannelFlutterWorkmanagerNotification();

  /// The default instance of [FlutterWorkmanagerNotificationPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterWorkmanagerNotification].
  static FlutterWorkmanagerNotificationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterWorkmanagerNotificationPlatform] when
  /// they register themselves.
  static set instance(FlutterWorkmanagerNotificationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<void> startWorkManager(int id,String title, String desc, String btn,bool test) => _instance.startWorkManager(id,title,desc,btn,test);

  Future<void> setAppStateObserver(NotificationObserver observer)async{
    await _instance.setAppStateObserver(observer);
  }

  Future<int?> getAppLaunchNotificationId() => _instance.getAppLaunchNotificationId();

  Future<void> startForegroundService(int id,String title, String desc) => _instance.startForegroundService(id,title,desc);

  Future<bool> requestNotificationPermission() => _instance.requestNotificationPermission();
  Future<bool> checkNotificationPermission() => _instance.checkNotificationPermission();
}
