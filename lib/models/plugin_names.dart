abstract class PluginNames {
  static const barcode = 'BARCODE';
  static const msr = 'MSR'; // (Magnetic Stripe Reader) input
  static const rfid = 'RFID'; // (Radio-frequency Identification) input
  static const serial = 'SERIAL'; // input
  static const voice = 'VOICE'; // input
  static const workflow = 'WORKFLOW'; // input
  static const bdf = 'BDF'; // (Basic Data Formatting) processing
  static const adf = 'ADF'; // (Advanced Data Formatting) processing
  static const tokens = 'TOKENS'; // (data formatting and ordering for Keystroke and IP output with UDI/Multi-barcode data) processing
  static const intent = 'INTENT'; // output
  static const keystroke = 'KEYSTROKE'; // output
  static const ip = 'IP'; // (Internet Protocol) output
  static const dcp = 'DCP'; // (Data Capture Plus) utilities
  static const ekb = 'EKB'; // (Enterprise Keyboard) utilities
}
