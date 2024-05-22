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
