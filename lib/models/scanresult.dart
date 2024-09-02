import 'package:scanwedge/scanwedge.dart';

class ScanResult {
  final String barcode;
  final BarcodeTypes barcodeType;
  final String hardwareLabelType;

  ScanResult({required this.barcode, required this.barcodeType, required this.hardwareLabelType});
  factory ScanResult.fromDatawedge(dynamic json) => ScanResult(
      barcode: json['barcode'],
      barcodeType: BarcodeTypes.values.firstWhere((element) => element.name == json['barcodeType'], orElse: () => BarcodeTypes.unknown),
      hardwareLabelType: json['hardwareLabelType']);
  ScanResult copyWith({String? barcode, BarcodeTypes? barcodeType, String? hardwareLabelType}) =>
      ScanResult(barcode: barcode ?? this.barcode, barcodeType: barcodeType ?? this.barcodeType, hardwareLabelType: hardwareLabelType ?? this.hardwareLabelType);
  @override
  String toString() => 'ScanResult($barcode:$barcodeType)';
}

@Deprecated('Use BarcodeLabelType instead')
enum BarcodeLabelType {
  labelTypeCode39,
  labelTypeCodabar,
  labelTypeCode128,
  labelTypeD2of5,
  labelTypeIata2of5,
  labelTypeI2of5,
  labelTypeCode93,
  labelTypeUpca,
  labelTypeUpce0,
  labelTypeUpce1,
  labelTypeEan8,
  labelTypeEan13,
  labelTypeMsi,
  labelTypeEan128,
  labelTypeTrioptic39,
  labelTypeBookland,
  labelTypeCoupon,
  labelTypeDatabarCoupon,
  labelTypeIsbt128,
  labelTypeCode32,
  labelTypePdf417,
  labelTypeMicropdf,
  labelTypeTlc39,
  labelTypeCode11,
  labelTypeMaxicode,
  labelTypeDatamatrix,
  labelTypeQrcode,
  labelTypeGs1Databar,
  labelTypeGs1DatabarLim,
  labelTypeGs1DatabarExp,
  labelTypeUspostnet,
  labelTypeUsplanet,
  labelTypeUkpostal,
  labelTypeJappostal,
  labelTypeAuspostal,
  labelTypeDutchpostal,
  labelTypeFinnishpostal4s,
  labelTypeCanpostal,
  labelTypeChinese2of5,
  labelTypeAztec,
  labelTypeMicroqr,
  labelTypeUs4state,
  labelTypeUs4stateFics,
  labelTypeCompositeAb,
  labelTypeCompositeC,
  labelTypeWebcode,
  labelTypeSignature,
  labelTypeKorean3of5,
  labelTypeMatrix2of5,
  labelTypeOcr,
  labelTypeHanxin,
  labelTypeMailmark,
  multicodeDataFormat,
  labelTypeGs1Datamatrix,
  labelTypeGs1Qrcode,
  labelTypeDotcode,
  labelTypeGridmatrix,
  labelTypeUndefined,
}
