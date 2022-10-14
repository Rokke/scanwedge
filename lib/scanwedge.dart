
import 'scanwedge_platform_interface.dart';

class Scanwedge {
  Future<String?> getPlatformVersion() {
    return ScanwedgePlatform.instance.getPlatformVersion();
  }
}
