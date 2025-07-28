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