import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'scanwedge_platform_interface.dart';

/// An implementation of [ScanwedgePlatform] that uses method channels.
class MethodChannelScanwedge extends ScanwedgePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('scanwedge');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> createProfile({required String profileName}) async {
    final res = await methodChannel.invokeMethod<bool>('createProfile', {'name': profileName});
    return res ?? false;
  }

  @override
  Future<bool> toggleScanning() async {
    final res = await methodChannel.invokeMethod<bool>('toggleScan');
    return res ?? false;
  }
}
