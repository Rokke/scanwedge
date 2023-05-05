import 'dart:convert';
import 'dart:developer';

import 'package:scanwedge/models/barcode_plugin.dart';
import 'package:scanwedge/scancommands.dart';
import 'package:scanwedge/scanwedge_channel.dart';

class ScanProfile {
  final bool disableKeystroke;
  final String profileName, packageName;
  final BarcodePlugin? barcodePlugin;
  final String configMode;
  ScanProfile({required this.profileName, this.configMode = ProfileCreateType.createIfNotExist, this.disableKeystroke = false, this.barcodePlugin, required this.packageName});
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
        if (barcodePlugin != null) barcodePlugin!.toMap,
        {
          'PARAM_LIST': {
            'intent_output_enabled': 'true',
            'intent_action': ScanCommands.profileIntentAction,
            'intent_delivery': '2',
          },
          'PLUGIN_NAME': 'INTENT',
          'RESET_CONFIG': 'true'
        }
      ],
      'APP_LIST': packageName,
      'RESET_CONFIG': 'true'
    };
    final result = await scanWedge.sendCommandBundle(command: ScanCommands.datawedgeSendSetConfig, parameter: json, sendResult: true);
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
