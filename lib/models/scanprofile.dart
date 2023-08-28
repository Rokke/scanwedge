import 'dart:convert';
import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:scanwedge/models/barcode_plugin.dart';
import 'package:scanwedge/scanwedge_channel.dart';

class ScanProfile {
  static const datawedgeSendSetConfig = 'com.symbol.datawedge.api.SET_CONFIG';

  // static const datawedgeSendAction = 'com.symbol.datawedge.api.SET_CONFIG';
  static const profileIntentAction = 'no.talgoe.scanwedge.scanwedge.SCAN';
  final bool disableKeystroke;
  final String profileName, packageName;
  final BarcodePlugin? barcodePlugin;
  final String configMode;

  ScanProfile({
    required this.profileName,
    this.configMode = ProfileCreateType.createIfNotExist,
    this.disableKeystroke = false,
    this.barcodePlugin,
    required this.packageName,
  });

  Future<bool> sendCommands(ScanwedgeChannel scanWedge) async {
    debugPrint('ScanProfile()');
    final json = {
      'PROFILE_NAME': profileName,
      'PROFILE_ENABLED': 'true',
      'CONFIG_MODE': configMode,
      'PLUGIN_CONFIG': [
        {
          'PLUGIN_NAME': 'KEYSTROKE',
          'PARAM_LIST': {
            'keystroke_output_enabled': (!disableKeystroke).toString(),
          },
        },
        {
          'PLUGIN_NAME': 'BDF',
          'OUTPUT_PLUGIN_NAME': 'KEYSTROKE',
          'RESET_CONFIG': 'false',
          'PARAM_LIST': {
            'bdf_enabled': 'true',
            'bdf_send_enter': 'true',
          },
        },
        if (barcodePlugin != null) barcodePlugin!.toMap,
        {
          'PLUGIN_NAME': 'INTENT',
          'RESET_CONFIG': 'true',
          'PARAM_LIST': {
            'intent_output_enabled': 'true',
            'intent_action': profileIntentAction,
            'intent_delivery': '2',
          },
        }
      ],
      'APP_LIST': packageName,
      'RESET_CONFIG': 'false'
    };
    log('ScanProfile: ${jsonEncode(json)}');
    await scanWedge.sendCommandBundle(
      command: datawedgeSendSetConfig,
      parameter: json,
      sendResult: true,
    );
    return true;
  }

  Map<String, dynamic> getProfile(
    Map<String, dynamic> child, {
    String childKeyName = 'PLUGIN_CONFIG',
  }) =>
      {
        'PROFILE_NAME': profileName,
        'PROFILE_ENABLED': 'true',
        'CONFIG_MODE': configMode,
        childKeyName: child,
      };
}

abstract class ProfileCreateType {
  static const update = 'UPDATE';
  static const createIfNotExist = 'CREATE_IF_NOT_EXIST';
  static const overwrite = 'OVERWRITE';
}
