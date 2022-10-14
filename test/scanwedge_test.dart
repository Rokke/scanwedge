import 'package:flutter_test/flutter_test.dart';
import 'package:scanwedge/scanwedge.dart';
import 'package:scanwedge/scanwedge_platform_interface.dart';
import 'package:scanwedge/scanwedge_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockScanwedgePlatform
    with MockPlatformInterfaceMixin
    implements ScanwedgePlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final ScanwedgePlatform initialPlatform = ScanwedgePlatform.instance;

  test('$MethodChannelScanwedge is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelScanwedge>());
  });

  test('getPlatformVersion', () async {
    Scanwedge scanwedgePlugin = Scanwedge();
    MockScanwedgePlatform fakePlatform = MockScanwedgePlatform();
    ScanwedgePlatform.instance = fakePlatform;

    expect(await scanwedgePlugin.getPlatformVersion(), '42');
  });
}
