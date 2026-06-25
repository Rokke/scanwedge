## 0.0.1
* Initial Open Source release

## 0.0.2
* Added [isZebra], [modelName], [productName], [osVersion], [packageName] and [manufacturer]
* Ignoring request if not a Zebra device
* Added possibility to enabled/disable given barcodetypes
* Better source documentation
* Give device information after initialization

## 0.0.4
* Breaking change, use scanwedge.interface when creating the scanwedge
* Added possibility to enable or disable barcodetypes
* deprecating isZebra, use isDeviceSupported instead
* Updated packaged and Dart SDK

## 0.1.0+6
* Fixed setting correct package name

## 1.0.0-beta.1
* Refactored most classes to be more generic and support several hardware types
* You should now instead use the createProfile function with the [ProfileModel] to create a profile

## 1.0.0-beta.2
* Fixed so that disable scanner on Honeywell also disable the scanner from working
* Added deviceName as property to the [ScanwedgeChannel]
* Added Datalogic support

## 1.0.0

## 1.0.1
* Datalogic devices send raw scanned data and not converted data based on prefix/suffix on wedge

## 1.0.2
* Android 14 support (added receiver)
* Added simple function for fetching batterystate. NB! This might be moved to a separate plugin in the future

## 1.0.3
* Added newland support thanks to @M-Ahal

## 1.0.4
* Bugfix when creating a default profile on Honeywell device

## 1.1.0
* Extended battery info: Added support for more battery properties and device types (Zebra, Honeywell, Samsung, etc.)
* Improved battery status mapping and parsing for multiple manufacturers
* Added new fields to ExtendedBatteryStatus (e.g., batteryLow, backupBatteryVoltage, healthPercentage, timeToEmpty, timeToFull, etc.)
* Refactored battery monitoring and status reporting for Android 14 and newer
* Improved platform checks and error handling in ScanwedgeChannel
* Fixed GS1DataMatrix not supported issue (#7)
* Added stopMonitoringBatteryStatus function
* Various bugfixes and code cleanups
* Fixed Fixes Rokke/scanwedge#9

## 1.1.1
* Log adjustment possibility in android component
* Fixed some battery decommission calculations
* Added battery extended info fetching in example app

## 1.1.2
* Added support for Urovo devices thanks to @pedromellofh

## 1.1.3
* Implemented disposal of the hardwarePlugin on initialization to prevent the multiplication return of the scan result with doing hot restarts of the app. Thanks to @FUAUAB

## 1.1.4-beta.1
* Migrated to built-in Kotlin so the plugin builds on Android Gradle Plugin (AGP) 9.0+, while still applying the Kotlin Gradle Plugin on AGP < 9 for backwards compatibility (#16)
* Device info is now passed across the platform channel as a keyed map instead of a pipe-delimited string, removing index-drift fragility and making it easy to add fields later (#17)
* Bumped the example app's Gradle wrapper to 8.13 (required by the bundled AGP)

## 1.1.4-beta.2
* Fixed battery voltage and temperature being truncated by integer division (e.g. 4339 mV reported as 4.0 V)
* Guarded `batteryPercentage` against a zero/negative `scale` (was crashing with Infinity→toInt, or returning a negative percentage)
* Guarded `batteryDecommissionPercentageLeft` against a divide-by-zero when the decommission threshold is 100
* Centralised BroadcastReceiver registration into an SDK-guarded `registerReceiverCompat` helper used by all hardware plugins; fixes a crash on Android 7 (API 24-25) Zebra devices where scanning silently never started, and guards future hardware types automatically
* Made receiver unregistration safe across all hardware plugins, so a failed registration can no longer crash dispose()/re-init
* `BarcodeConfig.fromMap` no longer throws on an unknown barcode type (falls back to `unknown`)
* Hardened `createProfile`/`sendCommand`/`sendCommandBundle` argument parsing to return a proper error instead of crashing on malformed input
* `sendCommandBundle` no longer hangs forever if the native result never arrives (15s timeout) and is guarded against duplicate completion
* Stopping the native battery monitor on stream cancellation and disposing a previous monitor on re-subscribe, preventing a leaked/duplicated receiver
* Fixed `intentLog` being dropped on deserialization when `extraMap` was null