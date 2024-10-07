import 'dart:async';
import 'dart:developer';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:scanwedge/scanwedge.dart';

enum SupportedDevice { zebra, honeywell, datalogic, invalid }

class ScanwedgeChannel {
  static const channel = 'scanwedge';
  static const _methodChannel = MethodChannel(channel);
  final _streamController = StreamController<ScanResult>.broadcast();
  final SupportedDevice supportedDevice;
  final String manufacturer, modelName, productName, osVersion, packageName, deviceName;
  String? _lastCompleterError;
  Completer<String>? completerSendCommandBundle;
  @Deprecated('This is for backwards compatibility, use isDeviceSupported instead')
  bool get isZebra => isDeviceSupported;
  bool get isDeviceSupported => supportedDevice != SupportedDevice.invalid;
  ScanwedgeChannel._({required this.supportedDevice, this.manufacturer = '', this.modelName = '', this.productName = '', this.osVersion = '', this.packageName = '', this.deviceName = ''}) {
    _methodChannel.setMethodCallHandler(_methodHandler);
    debugPrint('Scanwedge: $supportedDevice, $deviceName');
  }
  Stream<ScanResult> get stream => _streamController.stream;

  /// Returns if the given device is supported. This will also be used when calling the different methods
  /// Returns `null` if the device is not supported
  static Future<ScanwedgeChannel> initialize() async {
    try {
      if (!kIsWeb && Platform.isAndroid) {
        final supportedResponse = await _methodChannel.invokeMethod<String>('initializeDataWedge');
        debugPrint('initializeDataWedge: $supportedResponse');
        if (supportedResponse != null) {
          final devInfoString = supportedResponse.split('|');
          if (devInfoString.length > 5) {
            return ScanwedgeChannel._(
              supportedDevice: _fetchSupportedDevice(devInfoString[0]),
              manufacturer: devInfoString[1],
              modelName: devInfoString[2],
              productName: devInfoString[3],
              osVersion: devInfoString[4],
              packageName: devInfoString[5],
              deviceName: devInfoString[6],
            );
          }
        }
      }
    } catch (e) {
      debugPrint('initialize, Error: $e');
    }
    return ScanwedgeChannel._(supportedDevice: SupportedDevice.invalid);
  }

  static SupportedDevice _fetchSupportedDevice(String apiVersion) => switch (apiVersion) {
        'ZEBRA' => SupportedDevice.zebra,
        'HONEYWELL' => SupportedDevice.honeywell,
        'DATALOGIC' => SupportedDevice.datalogic,
        _ => SupportedDevice.invalid,
      };

  Future<void> _methodHandler(MethodCall call) async {
    debugPrint("_methodHandler($call, ${call.arguments})-${call.method}");
    try {
      switch (call.method) {
        case "scan":
          debugPrint("scan method ${call.arguments}");
          final scanResult = ScanResult.fromDatawedge(call.arguments);
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
    } catch (e) {
      debugPrint('_methodHandler, Error: $e');
    }
  }

  Future<bool> toggleScanning() async => isDeviceSupported ? await _methodChannel.invokeMethod<bool>('toggleScanning') ?? false : false;
  Future<bool> enableScanner() async => isDeviceSupported ? await _methodChannel.invokeMethod<bool>('enableScanner') ?? false : false;
  Future<bool> disableScanner() async => isDeviceSupported ? await _methodChannel.invokeMethod<bool>('disableScanner') ?? false : false;
  Future<bool> createProfile({required ProfileModel profile}) async {
    try {
      debugPrint('createProfile(${profile.toMap})-$isDeviceSupported');
      return isDeviceSupported ? await _methodChannel.invokeMethod<bool>('createProfile', profile.toMap) ?? false : false;
    } catch (e) {
      debugPrint('createProfile, Error: $e');
      return false;
    }
  }

  @Deprecated('This is for backwards compatibility and only support Zebra devices, this will be removed later')
  Future<bool> sendCommand({required String command, required String parameter}) async =>
      isDeviceSupported ? await _methodChannel.invokeMethod<bool>('sendCommand', {'command': command, 'parameter': parameter}) ?? false : false;

  @Deprecated('This is for backwards compatibility and only support Zebra devices, this will be removed later')
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
