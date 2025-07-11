import 'package:scanwedge/scanwedge.dart';

class ScanResult {
  final String barcode;
  final BarcodeTypes barcodeType;
  final String hardwareLabelType;
  final Map<String, String>? tokenMap;

  ScanResult({
    required this.barcode,
    required this.barcodeType,
    required this.hardwareLabelType,
    required this.tokenMap,
  });

  factory ScanResult.fromDatawedge(dynamic json) {
    // parse token: Map<String,String>?
    Map<String, String>? parsedTokenMap;
    if (json['tokens'] != null) {
      parsedTokenMap = Map<String, String>.from(json['tokens'] as Map);
    }

    return ScanResult(
      barcode: json['barcode'] as String,
      barcodeType: BarcodeTypes.values.firstWhere(
        (e) => e.name == json['barcodeType'],
        orElse: () => BarcodeTypes.unknown,
      ),
      hardwareLabelType: json['hardwareLabelType'] as String,
      tokenMap: parsedTokenMap,
    );
  }

  ScanResult copyWith({
    String? barcode,
    BarcodeTypes? barcodeType,
    String? hardwareLabelType,
    Map<String, String>? tokenMap,
  }) =>
      ScanResult(
        barcode: barcode ?? this.barcode,
        barcodeType: barcodeType ?? this.barcodeType,
        hardwareLabelType: hardwareLabelType ?? this.hardwareLabelType,
        tokenMap: tokenMap ?? this.tokenMap,
      );

  @override
  String toString() => 'ScanResult($barcode:$barcodeType)';

  String toStringLong() {
    final tokenStr =
        tokenMap != null ? tokenMap!.entries.map((e) => '${e.key}: ${e.value}').join(', ') : 'null';
    return 'ScanResult('
        'barcode: $barcode, '
        'barcodeType: $barcodeType, '
        'hardwareLabelType: $hardwareLabelType, '
        'tokenMap: $tokenStr'
        ')';
  }
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
