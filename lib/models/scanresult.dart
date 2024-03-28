class ScanResult {
  final String barcode;
  final BarcodeLabelType labelType;

  ScanResult({required this.barcode, required this.labelType});
  static BarcodeLabelType stringToBarcodeLabelType(String labelType) {
    switch (labelType) {
      case "CODE39":
        return BarcodeLabelType.labelTypeCode39;
      case "CODABAR":
        return BarcodeLabelType.labelTypeCodabar;
      case "CODE128":
        return BarcodeLabelType.labelTypeCode128;
      case "D2OF5":
        return BarcodeLabelType.labelTypeD2of5;
      case "IATA2OF5":
        return BarcodeLabelType.labelTypeIata2of5;
      case "I2OF5":
        return BarcodeLabelType.labelTypeI2of5;
      case "CODE93":
        return BarcodeLabelType.labelTypeCode93;
      case "UPCA":
        return BarcodeLabelType.labelTypeUpca;
      case "UPCE0":
        return BarcodeLabelType.labelTypeUpce0;
      case "UPCE1":
        return BarcodeLabelType.labelTypeUpce1;
      case "EAN8":
        return BarcodeLabelType.labelTypeEan8;
      case "EAN13":
        return BarcodeLabelType.labelTypeEan13;
      case "MSI":
        return BarcodeLabelType.labelTypeMsi;
      case "EAN128":
        return BarcodeLabelType.labelTypeEan128;
      case "TRIOPTIC39":
        return BarcodeLabelType.labelTypeTrioptic39;
      case "BOOKLAND":
        return BarcodeLabelType.labelTypeBookland;
      case "COUPON":
        return BarcodeLabelType.labelTypeCoupon;
      case "DATABAR-COUPON":
        return BarcodeLabelType.labelTypeDatabarCoupon;
      case "ISBT128":
        return BarcodeLabelType.labelTypeIsbt128;
      case "CODE32":
        return BarcodeLabelType.labelTypeCode32;
      case "PDF417":
        return BarcodeLabelType.labelTypePdf417;
      case "MICROPDF":
        return BarcodeLabelType.labelTypeMicropdf;
      case "TLC39":
        return BarcodeLabelType.labelTypeTlc39;
      case "CODE11":
        return BarcodeLabelType.labelTypeCode11;
      case "MAXICODE":
        return BarcodeLabelType.labelTypeMaxicode;
      case "DATAMATRIX":
        return BarcodeLabelType.labelTypeDatamatrix;
      case "QRCODE":
        return BarcodeLabelType.labelTypeQrcode;
      case "GS1-DATABAR":
        return BarcodeLabelType.labelTypeGs1Databar;
      case "GS1-DATABAR-LIM":
        return BarcodeLabelType.labelTypeGs1DatabarLim;
      case "GS1-DATABAR-EXP":
        return BarcodeLabelType.labelTypeGs1DatabarExp;
      case "USPOSTNET":
        return BarcodeLabelType.labelTypeUspostnet;
      case "USPLANET":
        return BarcodeLabelType.labelTypeUsplanet;
      case "UKPOSTAL":
        return BarcodeLabelType.labelTypeUkpostal;
      case "JAPPOSTAL":
        return BarcodeLabelType.labelTypeJappostal;
      case "AUSPOSTAL":
        return BarcodeLabelType.labelTypeAuspostal;
      case "DUTCHPOSTAL":
        return BarcodeLabelType.labelTypeDutchpostal;
      case "FINNISHPOSTAL-4S":
        return BarcodeLabelType.labelTypeFinnishpostal4s;
      case "CANPOSTAL":
        return BarcodeLabelType.labelTypeCanpostal;
      case "CHINESE-2OF5":
        return BarcodeLabelType.labelTypeChinese2of5;
      case "AZTEC":
        return BarcodeLabelType.labelTypeAztec;
      case "MICROQR":
        return BarcodeLabelType.labelTypeMicroqr;
      case "US4STATE":
        return BarcodeLabelType.labelTypeUs4state;
      case "US4STATE-FICS":
        return BarcodeLabelType.labelTypeUs4stateFics;
      case "COMPOSITE-AB":
        return BarcodeLabelType.labelTypeCompositeAb;
      case "COMPOSITE-C":
        return BarcodeLabelType.labelTypeCompositeC;
      case "WEBCODE":
        return BarcodeLabelType.labelTypeWebcode;
      case "SIGNATURE":
        return BarcodeLabelType.labelTypeSignature;
      case "KOREAN-3OF5":
        return BarcodeLabelType.labelTypeKorean3of5;
      case "MATRIX-2OF5":
        return BarcodeLabelType.labelTypeMatrix2of5;
      case "OCR":
        return BarcodeLabelType.labelTypeOcr;
      case "HANXIN":
        return BarcodeLabelType.labelTypeHanxin;
      case "MAILMARK":
        return BarcodeLabelType.labelTypeMailmark;
      case "MULTICODE-DATA-FORMAT":
        return BarcodeLabelType.multicodeDataFormat;
      case "GS1-DATAMATRIX":
        return BarcodeLabelType.labelTypeGs1Datamatrix;
      case "GS1-QRCODE":
        return BarcodeLabelType.labelTypeGs1Qrcode;
      case "DOTCODE":
        return BarcodeLabelType.labelTypeDotcode;
      case "GRIDMATRIX":
        return BarcodeLabelType.labelTypeGridmatrix;
      case "UNDEFINED":
      default:
        return BarcodeLabelType.labelTypeUndefined;
    }
  }

  factory ScanResult.fromDatawedge(dynamic json) => ScanResult(barcode: json['barcode'], labelType: stringToBarcodeLabelType(json['labelType']));
  @override
  String toString() => 'ScanResult($barcode, $labelType)';
}

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
