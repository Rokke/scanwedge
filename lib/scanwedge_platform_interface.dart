import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'scanwedge_method_channel.dart';

abstract class ScanwedgePlatform extends PlatformInterface {
  ScanwedgePlatform() : super(token: _token);

  static final Object _token = Object();

  static ScanwedgePlatform _instance = MethodChannelScanwedge();

  static ScanwedgePlatform get instance => _instance;

  static set instance(ScanwedgePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion();
  Future<bool> createProfile({required String profileName});
  Future<bool> toggleScanning();
}
