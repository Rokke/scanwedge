import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'scanwedge_method_channel.dart';

abstract class ScanwedgePlatform extends PlatformInterface {
  /// Constructs a ScanwedgePlatform.
  ScanwedgePlatform() : super(token: _token);

  static final Object _token = Object();

  static ScanwedgePlatform _instance = MethodChannelScanwedge();

  /// The default instance of [ScanwedgePlatform] to use.
  ///
  /// Defaults to [MethodChannelScanwedge].
  static ScanwedgePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ScanwedgePlatform] when
  /// they register themselves.
  static set instance(ScanwedgePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
