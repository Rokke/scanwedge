# Scanwedge

Scanwedge is a Flutter plugin for Zebra devices with DataWedge to receive barcodes and create profiles
It might support other devices later.
This will only work on Android Zebra devices but it will not crash if used on other devices

## Getting Started

### Commands
|Command|Description|
|-|-|
|**createProfile**|Created a new scanprofile, input is a [ScanProfile](#markdown-header-scanprofile) that|
|**enableScanner**|Enables the scanner|
|**disableScanner**|Disables the scanner|
|**suspendScanner**|Suspends the scanner|
|**resumeScanner**|Resumes the scanner|

>Difference between enable/disable and suspend/resume is that enable/disable will work even if the scanner is ready but suspend/resume is much faster but will not work if scanner is not ready

&nbsp;

### Creating a new basic profile
```dart
final _scanwedgePlugin = Scanwedge();

//[profileName] will be the name of the profile, [packageName] is the packageName of the host application
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
    this.aimType = AimType.trigger,             // See [AimType]
    this.timeoutBetweenScans = 0                // 0-5000 ms. Time between reading barcodes (for [AimType.presentation])
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