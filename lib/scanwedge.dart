import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:scanwedge/models/profile_model.dart';
import 'package:scanwedge/models/scanprofile.dart';
import 'package:scanwedge/models/scanresult.dart';
import 'package:scanwedge/scancommands.dart';
import 'package:scanwedge/scanwedge_channel.dart';

export 'package:scanwedge/models/aimtype.dart';
export 'package:scanwedge/models/barcode_plugin.dart';
export 'package:scanwedge/models/barcodetype_enum.dart';
export 'package:scanwedge/models/plugin_names.dart';
export 'package:scanwedge/models/profile_model.dart';
export 'package:scanwedge/models/scanprofile.dart';
export 'package:scanwedge/models/scanresult.dart';

class Scanwedge {
  final ScanwedgeChannel _scanwedgeChannel;
  Scanwedge._(this._scanwedgeChannel);
  static Future<Scanwedge> initialize() async {
    return Scanwedge._(await ScanwedgeChannel.initialize());
  }

  /// The stream that you will receive the [ScanResult] from
  Stream<ScanResult> get stream => _scanwedgeChannel.stream;

  /// Returns if the given device is supported. This will also be used when calling the different methods
  bool get isDeviceSupported => _scanwedgeChannel.isDeviceSupported;

  /// Gives the model name of the device it runs on. Example 'TC57'
  String get modelName => _scanwedgeChannel.modelName;

  /// Gives the product name of the device it runs on. Example 'TC57'
  String get productName => _scanwedgeChannel.productName;

  /// Gives the manufacturer name of the device it runs on. Example 'Zebra Technologies'
  String get manufacturer => _scanwedgeChannel.manufacturer;

  /// Gives the Android OS version that the device runs on. Example '10'
  String get osVersion => _scanwedgeChannel.osVersion;

  /// Returns the supported device as a [SupportedDevice] enum
  /// This can be used to check if the device is supported and what type of device it is
  SupportedDevice get supportedDevice => _scanwedgeChannel.supportedDevice;

  /// Gives the current host application (your packagename). Example 'no.talgoe.scanwedge.scanwedge_example'
  String get packageName => _scanwedgeChannel.packageName;
  // Future<bool> sendCommand(ScanProfile profile) =>ScanwedgePlatform.sendCommand(command: profile.command, parameter: profile.parameter);

  /// [profile] is the name for the package that will be visible in the profile list on the device.
  /// [packageName] inside the [ScanProfile] is optional and if omitted the packageName for this host application will be used witch is mostly what you want.
  /// If you want to use this application only to create profile for some other application, then you should use that applications package name instead
  @Deprecated('This is for backwards compatibility, use [createScanProfile] instead, honeywell will throw exception is this is called')
  Future<bool> createProfile(ScanProfile profile) => switch (_scanwedgeChannel.supportedDevice) {
        // SupportedDevice.honeywell => createScanProfile(profile),
        SupportedDevice.zebra => profile.sendCommands(_scanwedgeChannel),
        _ => throw UnsupportedError('This device is not supported')
      };

  /// [profile] is the [ProfileModel] that will be used to create the scanprofile on the device
  /// This supports both Zebra and Honeywell devices
  Future<bool> createScanProfile(ProfileModel profile) => _scanwedgeChannel.createProfile(profile: profile);

  /// Toggles the activation of the scanner (SOFTTRIGGER)
  Future<bool> toggleScanning() async {
    try {
      debugPrint('toggleScanning');
      return await _scanwedgeChannel.toggleScanning();
    } catch (e) {
      log('toggleScanning, Error: $e');
      return false;
    }
  }

  /// Disables the scanning
  Future<void> disableScanner() => _scanwedgeChannel.disableScanner();

  /// Enable the scanning
  Future<void> enableScanner() => _scanwedgeChannel.enableScanner();

  /// Suspends the DataWedge scanprofile, this is quicker than the [disableScanner]
  @Deprecated('This is for backwards compatibility, use disableScanner instead')
  Future<void> suspendScanner() => _scanwedgeChannel.sendCommand(command: ScanCommands.scannerInputPlugin, parameter: 'SUSPEND_PLUGIN');

  /// Resumes the DataWedge scanprofile, this is quicker than the [enableScanner]
  @Deprecated('This is for backwards compatibility, use enableScanner instead')
  Future<void> resumeScanner() => _scanwedgeChannel.sendCommand(command: ScanCommands.scannerInputPlugin, parameter: 'RESUME_PLUGIN');

  @Deprecated('This is for backwards compatibility and has no use')
  Future<String?> getDeviceInfo() => Future.value('DEPRECATED');

  /// Send basic simple commands to the DataWedge
  /// [command] is the command to be used
  /// [parameter] is the parameter to set for the [command]
  ///
  /// For more info on the commands go to <https://techdocs.zebra.com/datawedge/latest/guide/api/setconfig>
  Future<bool> sendCommand({required String command, required String parameter}) => _scanwedgeChannel.sendCommand(command: command, parameter: parameter);

  /// Send bundled commands to the DataWedge
  /// [command] command to be used
  /// [parameter] parameter with a Map of all the given parameters that should be set
  /// [sendResult] if the host application should receive the result back
  /// ```dart
  /// scanWedge.sendCommandBundle(datawedgeSendSetConfig, {'PLUGIN_NAME': PluginNames.barcode,
  ///   'PARAM_LIST':{
  ///     'scanner_input_enabled': 'true',
  ///     'configure_all_scanners': 'true
  ///   }
  /// });
  /// ```
  /// For more info on the commands go to <https://techdocs.zebra.com/datawedge/latest/guide/api/setconfig>
  Future<bool> sendCommandBundle({required String command, required Map<String, dynamic> parameter, bool sendResult = false}) =>
      _scanwedgeChannel.sendCommandBundle(command: command, parameter: parameter, sendResult: sendResult);
}
