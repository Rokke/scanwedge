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

## 1.1.4
* Migrated to built-in Kotlin so the plugin builds on Android Gradle Plugin (AGP) 9.0+, while still applying the Kotlin Gradle Plugin on AGP < 9 for backwards compatibility (#16)
* Device info is now passed across the platform channel as a keyed map instead of a pipe-delimited string, removing index-drift fragility and making it easy to add fields later (#17)
* Bumped the example app's Gradle wrapper to 8.13 (required by the bundled AGP)

## 1.1.5
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
* Fixed Honeywell GS1 DataBar mapping to the wrong decoder (`DEC_EAN128`, which collided with EAN128); GS1 DataBar now maps to `DEC_RSS_14` and GS1 DataBar Expanded to `DEC_RSS_EXPANDED` (previously unsupported on Honeywell) (#18)

## 1.1.6
* Fixed DataLogic profiles being silently rejected when a length was set on a symbology that has no length properties. GS1-128 in particular is an option of the Code 128 decoder (`CODE128_GS1_ENABLE`) and has no `CODE128_GS1_LENGTH*` properties — sending them failed the whole configuration COMMIT, so the scanner silently kept the previously applied profile and none of the requested barcodes were enabled (a profile asking for Code 128 20-20 alongside GS1-128 left the lengths of the profile before it in place). `BarcodeTypes.datalogicHasLengthControl()` now decides which symbologies get `_LENGTH_CONTROL`/`_LENGTH1`/`_LENGTH2`, and a dropped length is logged
* A DataLogic profile that enables GS1-128 now also enables the Code 128 decoder that actually decodes it
* The DataLogic property map is logged at info instead of debug — a rejected COMMIT gives no feedback, so this map is the only evidence of what the profile actually asked for
* Added `BarcodePlugin.withType()` and a `toString()` that shows the symbology and its length range

## 1.2.0
* Newland scan profiles now actually apply: `enabledBarcodes`, `keepDefaults`, lengths and the new `NewlandProfileModel` settings (#20). Thanks to @M-Ahal, who verified every setting and symbology name on a Newland NLS-MT95, and found DataBar and Micro QR reads coming back as `unknown` along the way.
* Newland: `initialize()` now switches the scanner to broadcast output. In any other output mode the scanner types into the focused field, so no scans reached the app unless the device had been switched to broadcast by hand. This, like every Newland profile setting, applies to the whole device and stays after the app exits — the `NewlandProfileModel` docs list what that means in practice
* Newland: GS1 DataBar, Code 93, Codabar, Interleaved 2 of 5, Aztec and MaxiCode reads now map to their `BarcodeTypes` instead of `unknown`
* Added `microqr` to the Dart `BarcodeTypes`. Only the Kotlin side had it, so Micro QR reads arrived as `unknown`. A new enum value can break an exhaustive `switch` over `BarcodeTypes`
* Added `NewlandProfileModel` and `NewlandTriggerMode`
* `SupportedDevice` is now exported from `package:scanwedge/scanwedge.dart`. `Scanwedge.supportedDevice` already returned it, but naming the type meant importing `scanwedge_channel.dart` directly
* `ExtendedBatteryStatus.createdAt` can now be supplied to the constructor instead of always being `DateTime.now()`. A live reading is unaffected - omitting it still stamps the moment the object is built - but a caller reconstructing a stored or synthetic reading can now say when it was actually taken. Screens that print the reading time could otherwise never be tested or captured deterministically
* `ExtendedBatteryStatus.fromJson` reads `createdAt` back, so it round-trips with `toJson`, which has always written it. A live intent never carries the field, so a reading straight off the device is unchanged
