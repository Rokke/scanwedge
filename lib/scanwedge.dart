import 'package:scanwedge/models/scanprofile.dart';

import 'scanwedge_platform_interface.dart';

export 'package:scanwedge/models/scanprofile.dart';

class Scanwedge {
  Future<String?> getPlatformVersion() {
    return ScanwedgePlatform.instance.getPlatformVersion();
  }

  Future<bool> createProfile(ScanProfile profile) {
    return ScanwedgePlatform.instance.createProfile(profileName: profile.profileName);
  }

  Future<bool> toggleScan() {
    return ScanwedgePlatform.instance.toggleScanning();
  }
}
