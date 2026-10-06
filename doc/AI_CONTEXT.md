# Scanwedge Package Documentation for AI Context

## Overview
**Package Name:** `scanwedge`
**Description:** A Flutter plugin designed to integrate with Android devices that feature dedicated hardware barcode scanners. It provides a unified API to control scanning hardware, manage scan profiles, and monitor device battery status across different manufacturers.

## Supported Manufacturers
This package supports Android devices from the following manufacturers:
- **Zebra**
- **Honeywell**
- **Datalogic**
- **Newland**
- **Urovo**

*Note: This plugin is Android-only. Calls on other platforms may throw errors or return default values.*

## Installation
Add the package to `pubspec.yaml`:
```yaml
dependencies:
  scanwedge: ^1.2.0 # Check for latest version
```

## Basic Usage

### 1. Initialization
You must initialize the plugin before using it.
```dart
import 'package:scanwedge/scanwedge.dart';

Scanwedge? _scanwedge;

Future<void> initScanner() async {
  _scanwedge = await Scanwedge.initialize();
}
```

### 2. Listening for Scans
Subscribe to the `stream` to receive scan results.
```dart
_scanwedge?.stream.listen((ScanResult result) {
  print('Barcode: ${result.barcode}');
  print('Type: ${result.barcodeType}'); // e.g., BarcodeTypes.code128
});
```

### 3. Creating a Scan Profile
A "profile" configures the hardware scanner (active barcode symbologies, output behavior, etc.).

**Generic Profile:**
```dart
await _scanwedge?.createScanProfile(
  ProfileModel(
    profileName: 'MyInfoProfile',
    enabledBarcodes: [
      BarcodeTypes.code128.create(),
      BarcodeTypes.qrCode.create(),
    ],
  ),
);
```

**Zebra Specific Profile (Advanced):**
```dart
await _scanwedge?.createScanProfile(
  ZebraProfileModel(
    profileName: 'MyZebraProfile',
    enabledBarcodes: [
      BarcodeConfig(barcodeType: BarcodeTypes.datamatrix),
    ],
    enableKeyStroke: false, // Prevent keyboard injection
    aimType: AimType.trigger, // Control trigger behavior
  ),
);
```

**Newland Specific Profile:**
```dart
await _scanwedge?.createScanProfile(
  NewlandProfileModel(
    profileName: 'MyNewlandProfile',
    enabledBarcodes: [
      BarcodeTypes.code128.create(minLength: 10, maxLength: 15),
      BarcodeTypes.ean13.create(),
    ],
    triggerMode: NewlandTriggerMode.pulse,
    scanTimeout: const Duration(seconds: 3),
  ),
);
```
Newland has no per-app profiles: every setting applies to the whole device and stays after the app exits (see `NewlandProfileModel` below).

### 4. Controlling the Scanner
```dart
// Soft trigger (simulate pressing the hardware button)
await _scanwedge?.toggleScanning();

// Disable the scanner hardware
await _scanwedge?.disableScanner();

// Enable the scanner hardware
await _scanwedge?.enableScanner();
```

### 5. Battery Monitoring
Get detailed battery health and status (especially useful for enterprise devices).

```dart
// Get single status
ExtendedBatteryStatus? status = await _scanwedge?.getExtendedBatteryStatus();

// Monitor changes
_scanwedge?.monitorBatteryStatus()?.then((stream) {
  stream.listen((status) {
    print('Battery Level: ${status.batteryPercentage}%');
    print('Health: ${status.health}');
  });
});
```

## API Reference (Simplified)

### `Scanwedge` Class
The main entry point.
- **Methods:**
  - `initialize()`: Static factory to create an instance.
  - `createScanProfile(ProfileModel)`: Configures the scanner.
  - `toggleScanning()`: Soft trigger.
  - `enableScanner()` / `disableScanner()`: Hardware control.
  - `monitorBatteryStatus()`: Returns a stream of battery updates.
- **Properties:**
  - `stream`: `Stream<ScanResult>` for scan events.
  - `manufacturer`: String (e.g., 'ZEBRA', 'Honeywell').
  - `isDeviceSupported`: bool.

### `ScanResult` Class
Represents a scanned barcode.
- `barcode`: String (The actual data).
- `barcodeType`: `BarcodeTypes` (The symbology, e.g., `code128`, `qrCode`; `unknown` when the plugin has no mapping for the device's label).
- `hardwareLabelType`: String (The raw label the device reported, e.g. `LABEL-TYPE-CODE128` on Zebra or `RSSFAMILY` on Newland).

### `ProfileModel` Class
Configuration for the scanner.
- `profileName`: String.
- `enabledBarcodes`: `List<BarcodeConfig>`.
- `keepDefaults`: bool (Whether to keep manufacturer default enabled types).

### `ZebraProfileModel` (Extends `ProfileModel`)
- `aimType`: `AimType` (e.g., `trigger`, `presentation`, `continuousRead`).
- `enableKeyStroke`: bool (Toggle keyboard emulation).

### `NewlandProfileModel` (Extends `ProfileModel`)
All fields are optional; a field left out keeps whatever the device currently has.
- `triggerMode`: `NewlandTriggerMode` (`level`, `continuous`, `pulse` (device default), `delay`).
- `scanTimeout`: `Duration` (one decode attempt, device caps it at 9 s).
- `rereadDelay`: `Duration` (how long the same barcode is ignored after a read).
- `scanInterval`: `Duration` (gap between attempts in continuous mode, device floors it at 50 ms).
- `soundOnScan`, `vibrateOnScan`, `ledOnScan`: bool (good-read feedback).
- `mainTriggerKey`, `leftTriggerKey`, `rightTriggerKey`, `pistolGripTrigger`: bool (which keys pull the trigger).
- `sendScanFailBroadcast`: bool (the plugin drops failed scans either way).

Newland changes are device-wide and persistent:
- `Scanwedge.initialize()` switches the scanner to broadcast output, so it stops typing into other apps.
- `keepDefaults: false` switches symbologies off; only Restore default in the scanner settings switches them back on.
- Restore default also switches broadcast output off, so scans stop arriving until `initialize()` runs again.
- `gs1DataBar` and `gs1DataBarExpanded` share one switch; on a CM60L engine both read back as `gs1DataBar`.
- Lengths persist between profiles. Out-of-range lengths, `0` included, are ignored rather than meaning "no limit".

### `BarcodeTypes` Enum
Supported symbologies include: `code128`, `code39`, `qrCode`, `datamatrix`, `ean13`, `upca`, `pdf417`, etc.
