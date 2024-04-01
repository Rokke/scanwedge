import 'package:scanwedge/models/aimtype.dart';
import 'package:scanwedge/models/barcodetype_enum.dart';

/// ProfileModel class
/// This generic class has the settings for the basic scanprofile
/// This can be extended with the hardware specific profiles like [HoneywellProfileModel] or [ZebraProfileModel]
/// It's no problem sending a hardware specific profile to a different hardware, the settings that are not supported will be ignored
class ProfileModel {
  String profileName;
  bool keepDefaults;
  List<BarcodeConfig>? enabledBarcodes;
  ProfileModel({required this.profileName, this.enabledBarcodes, this.keepDefaults = true});
  Map<String, dynamic>? get customMap => null;
  Map<String, dynamic> get toMap => {
        'name': profileName,
        if (enabledBarcodes != null) 'barcodes': enabledBarcodes!.map((e) => e.toMap).toList(),
        if (!keepDefaults) 'keepDefaults': keepDefaults,
        'hwConfig': customMap,
      };
}

/// HoneywellProfileModel class
/// This class extends the [ProfileModel] class
/// This currently have no additional settings from the [ProfileModel] but it might have more options later
class HoneywellProfileModel extends ProfileModel {
  HoneywellProfileModel({required super.profileName, super.enabledBarcodes, super.keepDefaults});
}

/// ZebraProfileModel class
/// This class extends the [ProfileModel] class
/// [aimType] can be set to different [AimType] like trigger or continuous
/// [enableKeyStroke] can be set to true if you want it to send the barcode to the input field, false is default
class ZebraProfileModel extends ProfileModel {
  final AimType? aimType;
  final bool? enableKeyStroke;
  ZebraProfileModel({required super.profileName, super.enabledBarcodes, super.keepDefaults, this.aimType, this.enableKeyStroke = false});

  @override
  Map<String, dynamic> get customMap => {
        'zebra': {
          //     'scanner_selection': 'auto',
          //     'scanner_input_enabled': 'true',
          if (aimType != null) 'aimType': aimType!.name,
          if (enableKeyStroke != null) 'enableKeyStroke': enableKeyStroke,
          //     'same_barcode_timeout': '0',
          //     ..._mapOfDisabledBarcodes,
          //     ..._mapOfEnabledBarcodes,
        },
        //   'PLUGIN_NAME': PluginNames.barcode,
        //   'RESET_CONFIG': 'true'
      };
}
