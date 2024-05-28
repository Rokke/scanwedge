import 'dart:convert';
import 'dart:developer';

import 'package:scanwedge/models/barcode_plugin.dart';
import 'package:scanwedge/scancommands.dart';
import 'package:scanwedge/scanwedge_channel.dart';

class ScanProfile {
  final bool disableKeystroke;
  final String profileName;
  final List<String> packageNames;
  final BarcodePlugin? barcodePlugin;
  final Map<String, dynamic>? optionalMap;
  final String configMode;
  final String? profileIntentAction;

  /// [profileName] is the name for the package that will be visible in the profile list on the device.
  /// [packageNames] inside the [ScanProfile] is optional and if omitted the packageName for this host application will be used witch is mostly what you want.
  /// [profileIntentAction] is the intent action that will be used when sending the scan result to the host application. This should only be used if you use this package only to create profile.
  /// When changing this value the scans will not be triggered on this package.
  ScanProfile({
    required this.profileName,
    this.configMode = ProfileCreateType.createIfNotExist,
    this.disableKeystroke = false,
    this.barcodePlugin,
    required this.packageNames,
    this.optionalMap,
    this.profileIntentAction = ScanCommands.profileIntentAction,
  });
  Future<bool> sendCommands(ScanwedgeChannel scanWedge) async {
    final json = {
      'PROFILE_NAME': profileName,
      'PROFILE_ENABLED': 'true',
      'CONFIG_MODE': configMode,
      'PLUGIN_CONFIG': [
        {
          'PARAM_LIST': {'keystroke_output_enabled': (!disableKeystroke).toString()},
          'PLUGIN_NAME': 'KEYSTROKE'
        },
        {
          'PARAM_LIST': {'inverse_1d_mode': 3},
          'PLUGIN_NAME': 'KEYSTROKE'
        },
        if (barcodePlugin != null) barcodePlugin!.toMap,
        if (optionalMap != null) optionalMap!,
        if (profileIntentAction != null)
          {
            'PARAM_LIST': {
              'intent_output_enabled': 'true',
              'intent_action': profileIntentAction,
              'intent_delivery': '2',
            },
            'PLUGIN_NAME': 'INTENT',
            'RESET_CONFIG': 'true'
          }
      ],
      'APP_LIST': packageNames
          .map((e) => {
                'PACKAGE_NAME': e,
                'ACTIVITY_LIST': ['*']
              })
          .toList(),
      'RESET_CONFIG': 'true'
    };
    final result =
        await scanWedge.sendCommandBundle(command: ScanCommands.datawedgeSendSetConfig, parameter: json, sendResult: true);
    log('ScanProfile: ${jsonEncode(json)}-$result');
    return result;
  }

  Map<String, dynamic> getProfile(Map<String, dynamic> child, {String childKeyName = 'PLUGIN_CONFIG'}) =>
      {'PROFILE_NAME': profileName, 'PROFILE_ENABLED': 'true', 'CONFIG_MODE': configMode, childKeyName: child};
}

abstract class ProfileCreateType {
  static const update = 'UPDATE';
  static const createIfNotExist = 'CREATE_IF_NOT_EXIST';
  static const overwrite = 'OVERWRITE';
}
