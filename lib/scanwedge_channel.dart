import 'dart:async';
import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:scanwedge/models/scanresult.dart';

class ScanwedgeChannel {
  static const channel = 'scanwedge';
  static final ScanwedgeChannel instance = ScanwedgeChannel._init();
  final MethodChannel _methodChannel = const MethodChannel(channel);
  final _streamController = StreamController<ScanResult>();
  Stream<ScanResult> get stream => _streamController.stream;
  ScanwedgeChannel._init() {
    _methodChannel.setMethodCallHandler(_methodHandler);
  }
  Future<void> _methodHandler(MethodCall call) async {
    debugPrint("_methodHandler($call, ${call.arguments})-${call.method}");
    log("_methodHandler($call, ${call.arguments})");
    switch (call.method) {
      case "test":
        log("test kalt: ${call.arguments}");
        break;
      case "scan":
        debugPrint("scan method ${call.arguments.runtimeType}");
        final scanResult = ScanResult.fromDatawedge(call.arguments);
        debugPrint("scan: $scanResult");
        _streamController.add(scanResult);
        break;
      default:
        debugPrint("invalid method: ${call.method}");
        break;
    }
  }

  Future<String?> getPlatformVersion() async => await _methodChannel.invokeMethod<String>('getPlatformVersion');

  Future<bool> createProfile({required String profileName, required String packageName}) async =>
      await _methodChannel.invokeMethod<bool>('createProfile', {'profileName': profileName, 'packageName': packageName}) ?? false;

  Future<bool> toggleScanning() async => await _methodChannel.invokeMethod<bool>('toggleScan') ?? false;

  Future<bool> sendCommand({required String command, required String parameter}) async => await _methodChannel.invokeMethod<bool>('sendCommand', {'command': command, 'parameter': parameter}) ?? false;
  Future<bool> sendCommandBundle({required String command, required Map<String, dynamic> parameter, required bool sendResult}) async =>
      await _methodChannel.invokeMethod<bool>('sendCommandBundle', {'command': command, 'parameter': parameter, 'sendResult': sendResult}) ?? false;
}
