import 'package:scanwedge/models/aimtype.dart';
import 'package:scanwedge/models/barcodetype_enum.dart';
import 'package:scanwedge/models/newland_trigger_mode.dart';

/// ProfileModel class
/// This generic class has the settings for the basic scanprofile
/// This can be extended with the hardware specific profiles like [HoneywellProfileModel] or [ZebraProfileModel]
/// It's no problem sending a hardware specific profile to a different hardware, the settings that are not supported will be ignored
/// ! For DataLogic devices the length min/max is inherited from previous setup so if this is not set for the codetype used it will use
///     the previously set length if any. You can set min/max length to 0 and it will set no length check for that specific codetype
///     ! Setting this to 0 will have consequences for other hardware manufacturers so be carefull with this.
///     But only use this for the allowed codetypes (not tested that all these works):
///       1D: Code 128, Code 39, I2of5, Codabar, Matrix 2 of 5, MSI, and Discrete 2 of 5
///       2D: Data Matrix, QR Code, Aztec Code, PDF417, Micro PDF417

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
  factory ProfileModel.fromMap(Map<String, dynamic> map) => ProfileModel(
        profileName: map['name'],
        enabledBarcodes: map['barcodes'] != null ? List<BarcodeConfig>.from(map['barcodes'].map((x) => BarcodeConfig.fromMap(x))) : null,
        keepDefaults: map['keepDefaults'] ?? true,
      );
}

/// HoneywellProfileModel class
/// This class extends the [ProfileModel] class
/// [enableEanCheckDigitTransmission] allows the last (13th) digit of an EAN-13 to be sent to with the barcode data
class HoneywellProfileModel extends ProfileModel {
  final bool enableEanCheckDigitTransmission;

  HoneywellProfileModel({
    required super.profileName,
    this.enableEanCheckDigitTransmission = true,
    super.enabledBarcodes,
    super.keepDefaults,
  });

  @override
  Map<String, dynamic> get customMap => {
        'honeywell': {
          'enableEanCheckDigitTransmission': enableEanCheckDigitTransmission,
        },
      };
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

/// Newland settings, sent on as `ACTION_BAR_SCANCFG` broadcasts.
///
/// Newland has no per-app profiles, so all of this changes the scanner for the whole device and stays
/// after your app exits:
///
/// - `Scanwedge.initialize()` switches the scanner to broadcast output, so it stops typing into other
///   apps.
/// - `keepDefaults: false` switches symbologies off, and only Restore default in the scanner settings
///   switches them back on.
/// - `gs1DataBar` and `gs1DataBarExpanded` share one switch, so asking for either enables both. On a
///   CM60L both also read back as `gs1DataBar`.
/// - The scanner keeps a `minLength` above its current max length, so set both.
/// - Out-of-range lengths, `0` included, are ignored rather than meaning "no limit".
class NewlandProfileModel extends ProfileModel {
  /// On by default. The plugin drops failed scans anyway, so switching it off only saves traffic.
  final bool? sendScanFailBroadcast;
  final NewlandTriggerMode? triggerMode;

  /// One decode attempt. The device caps it at 9 seconds.
  final Duration? scanTimeout;

  /// How long the same barcode is ignored after a read. Zero allows an immediate reread.
  final Duration? rereadDelay;

  /// Gap between decode attempts in continuous mode. The device floors it at 50 ms.
  final Duration? scanInterval;
  final bool? soundOnScan, vibrateOnScan, ledOnScan;
  final bool? mainTriggerKey, leftTriggerKey, rightTriggerKey, pistolGripTrigger;

  NewlandProfileModel({
    required super.profileName,
    super.enabledBarcodes,
    super.keepDefaults,
    this.sendScanFailBroadcast,
    this.triggerMode,
    this.scanTimeout,
    this.rereadDelay,
    this.scanInterval,
    this.soundOnScan,
    this.vibrateOnScan,
    this.ledOnScan,
    this.mainTriggerKey,
    this.leftTriggerKey,
    this.rightTriggerKey,
    this.pistolGripTrigger,
  });

  @override
  Map<String, dynamic> get customMap => {
        'newland': {
          if (sendScanFailBroadcast != null) 'sendScanFailBroadcast': sendScanFailBroadcast,
          if (triggerMode != null) 'triggerMode': triggerMode!.name,
          if (scanTimeout != null) 'scanTimeout': scanTimeout!.inMilliseconds,
          if (rereadDelay != null) 'rereadDelay': rereadDelay!.inMilliseconds,
          if (scanInterval != null) 'scanInterval': scanInterval!.inMilliseconds,
          if (soundOnScan != null) 'soundOnScan': soundOnScan,
          if (vibrateOnScan != null) 'vibrateOnScan': vibrateOnScan,
          if (ledOnScan != null) 'ledOnScan': ledOnScan,
          if (mainTriggerKey != null) 'mainTriggerKey': mainTriggerKey,
          if (leftTriggerKey != null) 'leftTriggerKey': leftTriggerKey,
          if (rightTriggerKey != null) 'rightTriggerKey': rightTriggerKey,
          if (pistolGripTrigger != null) 'pistolGripTrigger': pistolGripTrigger,
        },
      };
}
