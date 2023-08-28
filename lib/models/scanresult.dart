class ScanResult {
  final String barcode;
  final BarcodeLabelType labelType;

  ScanResult({required this.barcode, required this.labelType});

  static BarcodeLabelType stringToBarcodeLabelType(String labelType) {
    switch (labelType) {
      case "LABEL-TYPE-CODE39":
        return BarcodeLabelType.labelTypeCode39;
      case "LABEL-TYPE-CODABAR":
        return BarcodeLabelType.labelTypeCodabar;
      case "LABEL-TYPE-CODE128":
        return BarcodeLabelType.labelTypeCode128;
      case "LABEL-TYPE-D2OF5":
        return BarcodeLabelType.labelTypeD2of5;
      case "LABEL-TYPE-IATA2OF5":
        return BarcodeLabelType.labelTypeIata2of5;
      case "LABEL-TYPE-I2OF5":
        return BarcodeLabelType.labelTypeI2of5;
      case "LABEL-TYPE-CODE93":
        return BarcodeLabelType.labelTypeCode93;
      case "LABEL-TYPE-UPCA":
        return BarcodeLabelType.labelTypeUpca;
      case "LABEL-TYPE-UPCE0":
        return BarcodeLabelType.labelTypeUpce0;
      case "LABEL-TYPE-UPCE1":
        return BarcodeLabelType.labelTypeUpce1;
      case "LABEL-TYPE-EAN8":
        return BarcodeLabelType.labelTypeEan8;
      case "LABEL-TYPE-EAN13":
        return BarcodeLabelType.labelTypeEan13;
      case "LABEL-TYPE-MSI":
        return BarcodeLabelType.labelTypeMsi;
      case "LABEL-TYPE-EAN128":
        return BarcodeLabelType.labelTypeEan128;
      case "LABEL-TYPE-TRIOPTIC39":
        return BarcodeLabelType.labelTypeTrioptic39;
      case "LABEL-TYPE-BOOKLAND":
        return BarcodeLabelType.labelTypeBookland;
      case "LABEL-TYPE-COUPON":
        return BarcodeLabelType.labelTypeCoupon;
      case "LABEL-TYPE-DATABAR-COUPON":
        return BarcodeLabelType.labelTypeDatabarCoupon;
      case "LABEL-TYPE-ISBT128":
        return BarcodeLabelType.labelTypeIsbt128;
      case "LABEL-TYPE-CODE32":
        return BarcodeLabelType.labelTypeCode32;
      case "LABEL-TYPE-PDF417":
        return BarcodeLabelType.labelTypePdf417;
      case "LABEL-TYPE-MICROPDF":
        return BarcodeLabelType.labelTypeMicropdf;
      case "LABEL-TYPE-TLC39":
        return BarcodeLabelType.labelTypeTlc39;
      case "LABEL-TYPE-CODE11":
        return BarcodeLabelType.labelTypeCode11;
      case "LABEL-TYPE-MAXICODE":
        return BarcodeLabelType.labelTypeMaxicode;
      case "LABEL-TYPE-DATAMATRIX":
        return BarcodeLabelType.labelTypeDatamatrix;
      case "LABEL-TYPE-QRCODE":
        return BarcodeLabelType.labelTypeQrcode;
      case "LABEL-TYPE-GS1-DATABAR":
        return BarcodeLabelType.labelTypeGs1Databar;
      case "LABEL-TYPE-GS1-DATABAR-LIM":
        return BarcodeLabelType.labelTypeGs1DatabarLim;
      case "LABEL-TYPE-GS1-DATABAR-EXP":
        return BarcodeLabelType.labelTypeGs1DatabarExp;
      case "LABEL-TYPE-USPOSTNET":
        return BarcodeLabelType.labelTypeUspostnet;
      case "LABEL-TYPE-USPLANET":
        return BarcodeLabelType.labelTypeUsplanet;
      case "LABEL-TYPE-UKPOSTAL":
        return BarcodeLabelType.labelTypeUkpostal;
      case "LABEL-TYPE-JAPPOSTAL":
        return BarcodeLabelType.labelTypeJappostal;
      case "LABEL-TYPE-AUSPOSTAL":
        return BarcodeLabelType.labelTypeAuspostal;
      case "LABEL-TYPE-DUTCHPOSTAL":
        return BarcodeLabelType.labelTypeDutchpostal;
      case "LABEL-TYPE-FINNISHPOSTAL-4S":
        return BarcodeLabelType.labelTypeFinnishpostal4s;
      case "LABEL-TYPE-CANPOSTAL":
        return BarcodeLabelType.labelTypeCanpostal;
      case "LABEL-TYPE-CHINESE-2OF5":
        return BarcodeLabelType.labelTypeChinese2of5;
      case "LABEL-TYPE-AZTEC":
        return BarcodeLabelType.labelTypeAztec;
      case "LABEL-TYPE-MICROQR":
        return BarcodeLabelType.labelTypeMicroqr;
      case "LABEL-TYPE-US4STATE":
        return BarcodeLabelType.labelTypeUs4state;
      case "LABEL-TYPE-US4STATE-FICS":
        return BarcodeLabelType.labelTypeUs4stateFics;
      case "LABEL-TYPE-COMPOSITE-AB":
        return BarcodeLabelType.labelTypeCompositeAb;
      case "LABEL-TYPE-COMPOSITE-C":
        return BarcodeLabelType.labelTypeCompositeC;
      case "LABEL-TYPE-WEBCODE":
        return BarcodeLabelType.labelTypeWebcode;
      case "LABEL-TYPE-SIGNATURE":
        return BarcodeLabelType.labelTypeSignature;
      case "LABEL-TYPE-KOREAN-3OF5":
        return BarcodeLabelType.labelTypeKorean3of5;
      case "LABEL-TYPE-MATRIX-2OF5":
        return BarcodeLabelType.labelTypeMatrix2of5;
      case "LABEL-TYPE-OCR":
        return BarcodeLabelType.labelTypeOcr;
      case "LABEL-TYPE-HANXIN":
        return BarcodeLabelType.labelTypeHanxin;
      case "LABEL-TYPE-MAILMARK":
        return BarcodeLabelType.labelTypeMailmark;
      case "MULTICODE-DATA-FORMAT":
        return BarcodeLabelType.multicodeDataFormat;
      case "LABEL-TYPE-GS1-DATAMATRIX":
        return BarcodeLabelType.labelTypeGs1Datamatrix;
      case "LABEL-TYPE-GS1-QRCODE":
        return BarcodeLabelType.labelTypeGs1Qrcode;
      case "LABEL-TYPE-DOTCODE":
        return BarcodeLabelType.labelTypeDotcode;
      case "LABEL-TYPE-GRIDMATRIX":
        return BarcodeLabelType.labelTypeGridmatrix;
      case "LABEL-TYPE-UNDEFINED":
      default:
        return BarcodeLabelType.labelTypeUndefined;
    }
  }

  factory ScanResult.fromDatawedge(dynamic json) => ScanResult(
      barcode: json['barcode'],
      labelType: stringToBarcodeLabelType(json['labelType']));

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
