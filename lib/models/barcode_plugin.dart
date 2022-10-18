import 'package:scanwedge/models/aimtype.dart';

class BarcodePlugin {
  final AimType aimType;
  final int timeoutBetweenScans;

  BarcodePlugin({this.aimType = AimType.trigger, this.timeoutBetweenScans = 0});
  Map<String, dynamic> get toMap => {
        'PARAM_LIST': {
          'scanner_selection': 'auto',
          'scanner_input_enabled': 'true',
          'aim_type': AimType.values.indexOf(aimType).toString(),
          if (timeoutBetweenScans > 0) 'same_barcode_timeout': timeoutBetweenScans.toString(),
          // if (aimType == AimType.presentation) 'scene_detect_qualifier': '1',
        },
        'PLUGIN_NAME': 'BARCODE',
        'RESET_CONFIG': 'true'
      };
}
