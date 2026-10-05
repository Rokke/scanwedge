import 'package:flutter_test/flutter_test.dart';
import 'package:scanwedge/scanwedge.dart';

void main() {
  test('every Newland setting goes out under the key NewlandPlugin.kt reads', () {
    // A misspelt key is dropped on the Kotlin side without a word, so they're all spelt out here
    final profile = NewlandProfileModel(
      profileName: 'Test',
      sendScanFailBroadcast: false,
      triggerMode: NewlandTriggerMode.continuous,
      scanTimeout: const Duration(milliseconds: 7000),
      rereadDelay: const Duration(milliseconds: 300),
      scanInterval: const Duration(milliseconds: 80),
      soundOnScan: true,
      vibrateOnScan: false,
      ledOnScan: true,
      mainTriggerKey: false,
      leftTriggerKey: true,
      rightTriggerKey: false,
      pistolGripTrigger: true,
    );

    expect(profile.customMap, {
      'newland': {
        'sendScanFailBroadcast': false,
        'triggerMode': 'continuous',
        'scanTimeout': 7000,
        'rereadDelay': 300,
        'scanInterval': 80,
        'soundOnScan': true,
        'vibrateOnScan': false,
        'ledOnScan': true,
        'mainTriggerKey': false,
        'leftTriggerKey': true,
        'rightTriggerKey': false,
        'pistolGripTrigger': true,
      },
    });
  });
}
