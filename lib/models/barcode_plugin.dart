import 'package:scanwedge/scanwedge.dart';

@Deprecated('This is for backwards compatibility, use [BarcodeConfig] instead')
class BarcodePlugin {
  final AimType aimType;
  final int timeoutBetweenScans;
  final List<BarcodeLabelType>? enabledBarcodes, disabledBarcodes;

  BarcodePlugin({this.aimType = AimType.trigger, this.timeoutBetweenScans = 0, this.disabledBarcodes, this.enabledBarcodes});
  Map<String, dynamic> get toMap => {
        'PARAM_LIST': {
          'scanner_selection': 'auto',
          'scanner_input_enabled': 'true',
          'aim_type': AimType.values.indexOf(aimType).toString(),
          if (timeoutBetweenScans > 0) 'same_barcode_timeout': timeoutBetweenScans.toString(),
          ..._mapOfDisabledBarcodes,
          ..._mapOfEnabledBarcodes,
        },
        'PLUGIN_NAME': PluginNames.barcode,
        'RESET_CONFIG': 'true'
      };
  Map get _mapOfDisabledBarcodes => disabledBarcodes == null ? {} : {for (final entry in disabledBarcodes!) 'decoder_${fetchDecoderName(entry)}': 'false'};
  Map get _mapOfEnabledBarcodes => enabledBarcodes == null ? {} : {for (final entry in enabledBarcodes!) 'decoder_${fetchDecoderName(entry)}': 'true'};
  static String fetchDecoderName(BarcodeLabelType barcodeLabelType) => barcodeLabelType.name.split('.').last.substring(9).toLowerCase();
  @override
  toString() => toMap.toString();
}

@Deprecated('This is for backwards compatibility, use [BarcodeConfig] instead')
class BarcodeConfiguration {
  final List<BarcodeLabelType> barcodes;
  BarcodeConfiguration({required this.barcodes});
  List<Map> get toMap => [
        for (final barcode in barcodes)
          {
            'decoder_${BarcodePlugin.fetchDecoderName(barcode)}': 'true',
          }
      ];
}
