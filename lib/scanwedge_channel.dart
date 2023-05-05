import 'dart:async';
import 'dart:developer';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:scanwedge/models/scanresult.dart';

class ScanwedgeChannel {
  static const channel = 'scanwedge';
  // static final ScanwedgeChannel instance = ScanwedgeChannel._init();
  static const _methodChannel = MethodChannel(channel);
  final _streamController = StreamController<ScanResult>.broadcast();
  bool isZebra = false;
  //"${android.os.Build.MANUFACTURER}|${android.os.Build.MODEL}|${android.os.Build.PRODUCT}|${android.os.Build.VERSION.RELEASE}"
  String? _manufacturer, _model, _product, _osVersion, _packageName;
  bool get isDeviceSupported => isZebra;
  String get modelName => _model ?? '';
  String get productName => _product ?? '';
  String get manufacturer => _manufacturer ?? '';
  String get osVersion => _osVersion ?? '';
  String get packageName => _packageName ?? '';
  ScanwedgeChannel._({required String? deviceInfo}) {
    _methodChannel.setMethodCallHandler(_methodHandler);
    // getDeviceInfo().then((deviceInfo) {
    debugPrint('getDeviceInfo: $deviceInfo');
    if (deviceInfo != null) {
      final devInfoString = deviceInfo.split('|');
      _manufacturer = devInfoString.first;
      if (deviceInfo.length > 3) {
        _model = devInfoString[1];
        _product = devInfoString[2];
        _osVersion = devInfoString[3];
        isZebra = _manufacturer!.toUpperCase().startsWith('ZEBRA') || _model!.toUpperCase().startsWith('ZEBRA');
        debugPrint('deviceInfo($_manufacturer, $_model, $_product, $_osVersion)-$isZebra');
      }
    }
    // });
  }
  Stream<ScanResult> get stream => _streamController.stream;
  static Future<ScanwedgeChannel> initialize() async {
    debugPrint('init called');
    if (!kIsWeb && Platform.isAndroid) {
      final deviceInfo = await _methodChannel.invokeMethod<String>('getDeviceInfo');
      return ScanwedgeChannel._(deviceInfo: deviceInfo);
    }
    return ScanwedgeChannel._(deviceInfo: null);
  }

  Future<void> _methodHandler(MethodCall call) async {
    debugPrint("_methodHandler($call, ${call.arguments})-${call.method}");
    log("_methodHandler($call, ${call.arguments})");
    switch (call.method) {
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

  Future<String?> getDeviceInfo() async => await _methodChannel.invokeMethod<String>('getDeviceInfo');

  Future<bool> toggleScanning() async => isDeviceSupported ? await _methodChannel.invokeMethod<bool>('toggleScan') ?? false : false;

  Future<bool> sendCommand({required String command, required String parameter}) async =>
      isDeviceSupported ? await _methodChannel.invokeMethod<bool>('sendCommand', {'command': command, 'parameter': parameter}) ?? false : false;

  Future<bool> sendCommandBundle({required String command, required Map<String, dynamic> parameter, required bool sendResult}) async {
    debugPrint('sendCommandBundle-$isDeviceSupported');
    return isDeviceSupported ? await _methodChannel.invokeMethod<bool>('sendCommandBundle', {'command': command, 'parameter': parameter, 'sendResult': sendResult}) ?? false : false;
  }
}
