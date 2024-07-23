import 'package:scanwedge/scanwedge.dart';

// DOCS
// https://github.com/ltrudu/ZebraEntepriseServices/blob/e35cf2ef006c7d3fd1296312da8ab91aa7b5bdba/datawedgeprofileintentswrapper/src/main/java/com/zebra/datawedgeprofileintents/SettingsPlugins/PluginScanner.java#L186
class BarcodePlugin {
  final AimType aimType;
  final int timeoutBetweenScans;
  final List<BarcodeLabelType>? disabledBarcodes;

  BarcodePlugin({this.aimType = AimType.trigger, this.timeoutBetweenScans = 0, this.disabledBarcodes});
  Map<String, dynamic> get toMap => {
        'PARAM_LIST': {
          'scanner_selection': 'auto',
          'scanner_input_enabled': 'true',
          'inverse_1d_mode': '2',
          'decoder_upca_preamble': '2',
          'aim_type': AimType.values.indexOf(aimType).toString(),
          if (timeoutBetweenScans > 0) 'same_barcode_timeout': timeoutBetweenScans.toString(),
          ..._mapOfDisabledBarcodes,
          ..._mapOfEnabledBarcodes,
        },
        'PLUGIN_NAME': PluginNames.barcode,
        'RESET_CONFIG': 'true'
      };
  Map get _mapOfDisabledBarcodes =>
      disabledBarcodes == null ? {} : {for (final entry in disabledBarcodes!) 'decoder_${fetchDecoderName(entry)}': 'false'};
  Map get _mapOfEnabledBarcodes => {
        for (final entry in [
          BarcodeLabelType.labelTypeI2of5,
          BarcodeLabelType.labelTypeEan128,
          BarcodeLabelType.labelTypeEan13,
          BarcodeLabelType.labelTypeEan8,
          BarcodeLabelType.labelTypeQrcode,
        ])
          'decoder_${fetchDecoderName(entry)}': 'true'
      };
  static String fetchDecoderName(BarcodeLabelType barcodeLabelType) => barcodeLabelType.name.split('.').last.substring(9).toLowerCase();
  @override
  toString() => toMap.toString();
}
