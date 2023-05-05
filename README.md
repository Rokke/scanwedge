# Scanwedge

Scanwedge is a Flutter plugin for Zebra devices with DataWedge to receive barcodes and create profiles.<br>
It might support other devices later.<br>
This will only work on Android Zebra devices, but it will not have any negative impact on other devices.<br>
Code inspired

## Getting Started

### Commands
|Command|Description|
|-|-|
|**createProfile**|Created a new scanprofile, input is a [ScanProfile](#markdown-header-scanprofile) that decide rules for the scanprofile created|
|**enableScanner**|Enables the scanner|
|**disableScanner**|Disables the scanner|
|**suspendScanner**|Suspends the scanner|
|**resumeScanner**|Resumes the scanner|
|**isZebra**|Returns true if it's a Zebra device, if this is false no methods will be called on the device|
|**modelName**|Returns the modelname of the device|
|**productName**|Returns the productname of the device|
|**osVersion**|Returns the OS version on the device|
|**manufacturer**|Returns the manufacturer of the device|
|**packageName**|Returns the package name of the host application, this will also be used as default package name in [ScanProfile] if not set|

>Difference between enable/disable and suspend/resume is that enable/disable will work even if the scanner is ready but suspend/resume is much faster but will not work if scanner is not ready

&nbsp;

### Creating a new basic profile
```dart
final _scanwedgePlugin = Scanwedge();

//[profileName] will be the name of the profile, [packageName] is optional and the packageName of the host application to receive the intent
final newProfile=ScanProfile(profileName: 'TestProfile', disableKeystroke: true, packageName: 'no.talgoe.scanwedge.scanwedge_example')
_scanwedgePlugin.createProfile(newProfile)
```

### ScanProfile
This class can modify the profile that will be created
```dart
ScanProfile({
    required this.profileName,                      // The name of the profile
    this.configMode = ProfileCreateType.update,     // update, createIfNotExist, overwrite
    this.disableKeystroke = false,                  // disable sending scans to keyboard buffer
    this.barcodePlugin = BarcodePlugin(),           // See [BarcodePlugin]
    required this.packageName,                      // The packageName of your application.
});
```

### BarcodePlugin
Possibility to set configuration related to barcode scanning.
This will later have the possibility to enable/disable different barcodetypes
```dart
BarcodePlugin({
    this.aimType = AimType.trigger,   // See [AimTypes]
    this.timeoutBetweenScans = 0,     // 0-5000 ms. Time between reading barcodes (for [AimType.presentation])
    this.disabledBarcodes,            // A list of BarcodeLabelType that will be disabled in the profile
    this.enabledBarcodes,             // A list of BarcodeLabelType that will be enabled in the profile
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

#### BarcodeLabelType
````dart
  labelTypeCode39,
  labelTypeCodabar,
  labelTypeCode128,
  labelTypeD2of5,
  labelTypeIata2of5,
  labelTypeI2of5,
  labelTypeCode93,
  labelTypeUpca,
  labelTypeUpce0,
  labelTypeUpce1,
  labelTypeEan8,
  labelTypeEan13,
  labelTypeMsi,
  labelTypeEan128,
  labelTypeTrioptic39,
  labelTypeBookland,
  labelTypeCoupon,
  labelTypeDatabarCoupon,
  labelTypeIsbt128,
  labelTypeCode32,
  labelTypePdf417,
  labelTypeMicropdf,
  labelTypeTlc39,
  labelTypeCode11,
  labelTypeMaxicode,
  labelTypeDatamatrix,
  labelTypeQrcode,
  labelTypeGs1Databar,
  labelTypeGs1DatabarLim,
  labelTypeGs1DatabarExp,
  labelTypeUspostnet,
  labelTypeUsplanet,
  labelTypeUkpostal,
  labelTypeJappostal,
  labelTypeAuspostal,
  labelTypeDutchpostal,
  labelTypeFinnishpostal4s,
  labelTypeCanpostal,
  labelTypeChinese2of5,
  labelTypeAztec,
  labelTypeMicroqr,
  labelTypeUs4state,
  labelTypeUs4stateFics,
  labelTypeCompositeAb,
  labelTypeCompositeC,
  labelTypeWebcode,
  labelTypeSignature,
  labelTypeKorean3of5,
  labelTypeMatrix2of5,
  labelTypeOcr,
  labelTypeHanxin,
  labelTypeMailmark,
  multicodeDataFormat,
  labelTypeGs1Datamatrix,
  labelTypeGs1Qrcode,
  labelTypeDotcode,
  labelTypeGridmatrix,
  labelTypeUndefined,
````