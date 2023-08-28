import 'package:scanwedge/models/scanprofile.dart';
import 'package:scanwedge/models/scanresult.dart';
import 'package:scanwedge/scancommands.dart';
import 'package:scanwedge/scanwedge_channel.dart';

export 'package:scanwedge/models/scanprofile.dart';
export 'package:scanwedge/models/scanresult.dart';

class Scanwedge {
  Scanwedge();

  Stream<ScanResult> get stream => ScanwedgeChannel.instance.stream;

  // Future<String?> getPlatformVersion() =>
  //     ScanwedgeChannel.instance.getPlatformVersion();

  // Future<bool> sendCommand(ScanProfile profile) => ScanwedgePlatform.instance
  //     .sendCommand(command: profile.command, parameter: profile.parameter);

  Future<bool> createProfile(ScanProfile profile) =>
      profile.sendCommands(ScanwedgeChannel.instance);

  Future<bool> toggleScanning() => ScanCommands.toggleScanning();

  Future<void> disableScanner() => ScanCommands.disableScanner();

  Future<void> enableScanner() => ScanCommands.enableScanner();

  Future<void> suspendScanner() => ScanCommands.suspendScanner();

  Future<void> resumeScanner() => ScanCommands.resumeScanner();
}
