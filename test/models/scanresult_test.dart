import 'package:flutter_test/flutter_test.dart';
import 'package:scanwedge/models/scanresult.dart';

void main() {
  test('a Micro QR read keeps its type instead of falling back to unknown', () {
    // The payload a Newland MT95 sent for a Micro QR label
    final result = ScanResult.fromDatawedge({'barcode': 'ABC-abc-1234', 'barcodeType': 'microqr', 'hardwareLabelType': 'MICROQR'});

    expect(result.barcodeType.name, 'microqr');
  });
}
