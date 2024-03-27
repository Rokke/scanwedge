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
  bool _supportedDevice = false;
  //"${android.os.Build.MANUFACTURER}|${android.os.Build.MODEL}|${android.os.Build.PRODUCT}|${android.os.Build.VERSION.RELEASE}"
  String? _manufacturer, _model, _product, _osVersion, _packageName, _lastCompleterError;
  Completer<String>? completerSendCommandBundle;
  @Deprecated('This is for backwards compatibility, use isDeviceSupported instead')
  bool get isZebra => isDeviceSupported;
  bool get isDeviceSupported => _supportedDevice;
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
        _packageName = devInfoString[4];
        _supportedDevice = _manufacturer!.toUpperCase().startsWith('ZEBRA') || _model!.toUpperCase().startsWith('ZEBRA');
        debugPrint('deviceInfo($_manufacturer, $_model, $_product, $_osVersion)-$_supportedDevice');
      }
    }
    // });
  }
  Stream<ScanResult> get stream => _streamController.stream;

  /// Returns if the given device is supported. This will also be used when calling the different methods
  /// Returns `null` if the device is not supported
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
    switch (call.method) {
      case "scan":
        debugPrint("scan method ${call.arguments.runtimeType}");
        final scanResult = ScanResult.fromDatawedge(call.arguments);
        debugPrint("scan: $scanResult");
        _streamController.add(scanResult);
        break;
      case "result":
        debugPrint("result method ${call.arguments}");
        if (call.arguments["modules"] is List) {
          for (final element in call.arguments["modules"]) {
            if (element["result"] != "SUCCESS") {
              completerSendCommandBundle?.complete(element["module"]);
              return;
            }
          }
          completerSendCommandBundle?.complete("OK");
        } else {
          debugPrint("result method not list: ${call.arguments["modules"]}");
          completerSendCommandBundle?.complete(call.arguments);
        }
        log('result: ${completerSendCommandBundle?.future}');
        break;
      default:
        debugPrint("invalid method: ${call.method}-${call.arguments}");
        break;
    }
  }

  Future<String?> getDeviceInfo() async => await _methodChannel.invokeMethod<String>('getDeviceInfo');

  Future<bool> toggleScanning() async => isDeviceSupported ? await _methodChannel.invokeMethod<bool>('toggleScan') ?? false : false;

  Future<bool> sendCommand({required String command, required String parameter}) async =>
      isDeviceSupported ? await _methodChannel.invokeMethod<bool>('sendCommand', {'command': command, 'parameter': parameter}) ?? false : false;

  Future<bool> sendCommandBundle({required String command, required Map<String, dynamic> parameter, required bool sendResult}) async {
    if (!isDeviceSupported) return false;
    debugPrint('sendCommandBundle-$isDeviceSupported');
    if (sendResult) completerSendCommandBundle = Completer();
    final invoked = await _methodChannel.invokeMethod<bool>('sendCommandBundle', {'command': command, 'parameter': parameter, 'sendResult': sendResult});
    if (invoked == true && sendResult) {
      _lastCompleterError = await completerSendCommandBundle!.future;
      log('sendCommandBundle: $_lastCompleterError');
      return _lastCompleterError == 'OK';
    }
    return invoked == true;
  }
}
