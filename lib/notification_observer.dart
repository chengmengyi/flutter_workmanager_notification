class NotificationObserver{
  const NotificationObserver({
    required this.clickNotification,
  });

  final void Function(int notificationId) clickNotification;
}