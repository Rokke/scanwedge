enum BarcodeTypes {
  aztec,
  codabar,
  code128,
  code39,
  code93,
  datamatrix,
  ean128,
  ean13,
  ean8,
  gs1DataBar,
  gs1DataBarExpanded,
  i2of5,
  mailmark,
  maxicode,
  pdf417,
  qrCode,
  upca,
  upce0,
  unknown,
  manual,
  ;

  BarcodeConfig create({int? minLength, int? maxLength}) => BarcodeConfig(barcodeType: this, minLength: minLength, maxLength: maxLength);
}

class BarcodeConfig {
  final BarcodeTypes barcodeType;

  /// The minimum and maximum length of the barcode. If omitted, the barcode can be of any length
  /// Note that this is not supported on all barcode types so you should check the hardware vendors documentation for the specific barcode type (or test).
  final int? minLength, maxLength;

  BarcodeConfig({required this.barcodeType, this.minLength, this.maxLength});
  Map<String, dynamic> get toMap => {
        'type': barcodeType.name,
        if (minLength != null) 'minLength': minLength!,
        if (maxLength != null) 'maxLength': maxLength!,
      };
}
