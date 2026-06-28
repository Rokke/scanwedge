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
  scanwedge: ^1.1.2 # Check for latest version
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
- `barcodeType`: `BarcodeTypes` (The symbology, e.g., `code128`, `qrCode`).

### `ProfileModel` Class
Configuration for the scanner.
- `profileName`: String.
- `enabledBarcodes`: `List<BarcodeConfig>`.
- `keepDefaults`: bool (Whether to keep manufacturer default enabled types).

### `ZebraProfileModel` (Extends `ProfileModel`)
- `aimType`: `AimType` (e.g., `trigger`, `presentation`, `continuousRead`).
- `enableKeyStroke`: bool (Toggle keyboard emulation).

### `BarcodeTypes` Enum
Supported symbologies include: `code128`, `code39`, `qrCode`, `datamatrix`, `ean13`, `upca`, `pdf417`, etc.
