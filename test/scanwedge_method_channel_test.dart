import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:scanwedge/scanwedge_method_channel.dart';

void main() {
  MethodChannelScanwedge platform = MethodChannelScanwedge();
  const MethodChannel channel = MethodChannel('scanwedge');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
