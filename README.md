# Scanwedge

Scanwedge is a Flutter plugin for Android devices that have hardware barcode scanner functionality.<br>
Currently it supports Honeywell and Zebra devices.<br>
This will only work for these Android devices, but it will not have any negative impact on other devices.<br>
Code inspired by sample code from Honeywell, Zebra and ofcourse the whole Flutter community.

## Getting Started

### Commands
|Command|Description|
|-|-|
|**createProfile**|Created a new scanner profile, input is a [ProfileModel](#markdown-header-profilemodel)|
|**disableScanner**|Disables the scanner, for Honeywell devices it will still "read" but not send the result|
|**enableScanner**|Enables the scanner|
|**initialize**|Requests and initialize the Scanwedge, this must be called before using the Scanwedge|
|**isDeviceSupported**|Returns true if it's a supported device(Honeywell or Zebra), if this is false the other methods will be ignored when called|
|**manufacturer**|Returns the manufacturer of the device|
|**modelName**|Returns the modelname of the device|
|**osVersion**|Returns the OS version on the device|
|**packageName**|Returns the package name of the host application, this will also be used as default package name in [ScanProfile] if not set|
|**productName**|Returns the productname of the device|
|**supportedDevice**|Returns a [SupportedDevice] object with the information if the device is supported and the type|
|**stream**|Request a stream of barcode scans, returns barcodes scanned with the [ScanResult]|
|**toggleScanning**|Triggers a scan (SOFTTRIGGER)|

&nbsp;

### Creating a new basic profile
```dart
final _scanwedgePlugin = await Scanwedge.initialize();

//Creating a new profile with the name TestProfile that only reads CODE128 barcodes with the length between 5 and 10
_scanwedgePlugin.createProfile(ProfileModel(profileName: 'TestProfile', enabledBarcodes: [BarcodeTypes.code128.create(minLength: 5, maxLength: 10)]))
```

### ProfileModel
This class sets the scan profile
```dart
ProfileModel({
    required String profileName,                      // The name of the profile
    List<BarcodeConfig>? enabledBarcodes,             // A list of [BarcodeConfig] that will be enabled in the profile
    bool keepDefaults = true,                         // If true, the default enabled barcodes from the hardware used will be kept (together with [enabledBarcodes])
});
```

### ZebraProfileModel
This class is a extended version of [ProfileModel] that adds more configuration options if the device is a Zebra device (will also work with other devices but then ignore the extra configuration)
The extra options are:
[aimType] - The type of aim, see [AimTypes], default is [AimType.trigger]
[enableKeyStroke] - Enable sending scans to keyboard buffer (this is default set to false)

### HoneywellProfileModel
This currently have no extra configuration options, so should use [ProfileModel] instead

### SupportedDevice
This class is returned when calling [supportedDevice]
```dart
enum SupportedDevice { zebra, honeywell, invalid }
```

### BarcodeConfig
Possibility to set configuration related to barcode scanning.
```dart
BarcodeConfig({
    required BarcodeTypes barcodeType,  // The [BarcodeTypes]
    int? minLength,                     // The minimum length of the barcode, ignored if null. Not all barcode types support this so check hardware vendor for the appropriate barcode type
    int? maxLength,                     // The maximum length of the barcode, ignored if null. Not all barcode types support this so check hardware vendor for the appropriate barcode type
});
```
### Listening for scan events
To receive the scan events you can listen to the Stream
```dart
final _scanwedgePlugin=ScanwedgeChannel();
@override build()=>Card(
    child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
            const Text('Last scan:'),
            StreamBuilder(
                stream: _scanwedgePlugin.stream,
                builder: ((context, snapshot) => Text(
                        snapshot.hasData
                            ? snapshot.data.toString()
                            : snapshot.hasError
                                ? snapshot.error.toString()
                                : 'Scan something',
                        style: Theme.of(context).textTheme.titleMedium,
                    ))),
        ]
    )
);
```

#### AimTypes
This is a enum that sets the aim type for the scanner on Zebra devices
```dart
trigger
timedHold
timedRelease
pressAndRelease
presentation
continuousRead
pressAndSustain
pressAndContinue
timedContinuous
```

#### BarcodeTypes
````dart
aztec,
codabar,
code128,
code39,
code93,
datamatrix,
ean128,
ean13,
ean8,
gs1DataBar,
gs1DataBarExpanded,
i2of5,
mailmark,
maxicode,
pdf417,
qrCode,
upca,
upce0,
manual,     // Used for marking a barcode as manual input
unknown     // This is when it receives a unknown barcode, then check the [ScanResult.hardwareBarcodeType] for the actual barcode type
````