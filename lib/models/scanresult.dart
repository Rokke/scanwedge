import 'package:scanwedge/scanwedge.dart';

class ScanResult {
  final String barcode;
  final BarcodeTypes barcodeType;
  final String hardwareLabelType;
  final List<int>? rawBarcodeData;
  final List<Map<String, String>>? tokens;

  ScanResult({
    required this.barcode,
    required this.barcodeType,
    required this.hardwareLabelType,
    required this.rawBarcodeData,
    required this.tokens,
  });

  factory ScanResult.fromDatawedge(dynamic json) {
    // parse tokens: List<Map<String,String>>
    List<Map<String, String>>? parsedTokens;
    if (json['tokens'] != null) {
      parsedTokens =
          (json['tokens'] as List).map((e) => Map<String, String>.from(e as Map)).toList();
    }

    return ScanResult(
      barcode: json['barcode'] as String,
      barcodeType: BarcodeTypes.values.firstWhere(
        (element) => element.name == json['barcodeType'],
        orElse: () => BarcodeTypes.unknown,
      ),
      hardwareLabelType: json['hardwareLabelType'] as String,
      rawBarcodeData:
          json['rawBarcodeData'] != null ? List<int>.from(json['rawBarcodeData'] as List) : null,
      tokens: parsedTokens,
    );
  }

  ScanResult copyWith({
    String? barcode,
    BarcodeTypes? barcodeType,
    String? hardwareLabelType,
    List<int>? rawBarcodeData,
    List<Map<String, String>>? tokens,
  }) =>
      ScanResult(
        barcode: barcode ?? this.barcode,
        barcodeType: barcodeType ?? this.barcodeType,
        hardwareLabelType: hardwareLabelType ?? this.hardwareLabelType,
        rawBarcodeData: rawBarcodeData ?? this.rawBarcodeData,
        tokens: tokens ?? this.tokens,
      );

  @override
  String toString() => 'ScanResult($barcode:$barcodeType)';

  String toStringLong() {
    final rawHex =
        rawBarcodeData?.map((b) => b.toRadixString(16).padLeft(2, '0')).join(', ') ?? 'null';
    final tokenStr = tokens?.map((t) => '(${t['ai']}: ${t['value']})').join(', ') ?? 'null';
    return 'ScanResult('
        'barcode: $barcode, '
        'barcodeType: $barcodeType, '
        'hardwareLabelType: $hardwareLabelType, '
        'rawBarcodeData: [$rawHex], '
        'tokens: [$tokenStr]'
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
