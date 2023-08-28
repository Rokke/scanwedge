import 'package:scanwedge/scanwedge_channel.dart';

class ScanCommands {
  static const scannerInputPlugin =
      'com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN';

  static Future<bool> enableScanner() => ScanwedgeChannel.instance
      .sendCommand(command: scannerInputPlugin, parameter: 'ENABLE_PLUGIN');

  static Future<bool> disableScanner() => ScanwedgeChannel.instance
      .sendCommand(command: scannerInputPlugin, parameter: 'DISABLE_PLUGIN');

  static Future<bool> suspendScanner() => ScanwedgeChannel.instance
      .sendCommand(command: scannerInputPlugin, parameter: 'SUSPEND_PLUGIN');

  static Future<bool> resumeScanner() => ScanwedgeChannel.instance
      .sendCommand(command: scannerInputPlugin, parameter: 'RESUME_PLUGIN');

  static Future<bool> toggleScanning() =>
      ScanwedgeChannel.instance.toggleScanning();
}
