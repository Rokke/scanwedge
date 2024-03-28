import 'dart:developer';
import 'dart:io';

import 'package:commonui/commonui.dart';
import 'package:flutter/material.dart';
import 'package:scanwedge/models/barcode_plugin.dart';
import 'package:scanwedge/scanwedge.dart';

void main() {
  CommonLogger.initializeLogger(logName: 'MDMDevice', logLevel: ClassLoggerLevel.debug);
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with ClassLogger {
  Scanwedge? _scanwedgePlugin;
  String? _deviceInfo;
  final notifierDisableKeystroke = ValueNotifier(true);
  final notifierAimType = ValueNotifier(AimType.trigger);

  @override
  void initState() {
    super.initState();
    Scanwedge.initialize().then((scanwedge) {
      _scanwedgePlugin = scanwedge;
      setState(() {});
      scanwedge.getDeviceInfo().then((devInfo) => setState(() {
            _deviceInfo = devInfo;
          }));
    });
  }

  _createProfile() async {
    log('_createProfile()-${await _scanwedgePlugin?.createScanProfile(ScanProfile(profileName: 'TestProfile', disableKeystroke: notifierDisableKeystroke.value, packageNames: [
          'no.talgoe.scanwedge.scanwedge_example'
        ], barcodePlugin: BarcodePlugin(aimType: notifierAimType.value)))}');
  }

  @override
  Widget build(BuildContext context) {
    logD('build', 'build');
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
          actions: [
            IconButton(
                onPressed: () => debugPrint('${BarcodePlugin(disabledBarcodes: [
                          BarcodeLabelType.labelTypeCode11,
                          BarcodeLabelType.labelTypeCode128
                        ], enabledBarcodes: [
                          BarcodeLabelType.labelTypeCode39,
                          BarcodeLabelType.labelTypeCode93
                        ])..toMap}'),
                icon: const Icon(Icons.bathtub)),
            IconButton(onPressed: () => exit(0), icon: const Icon(Icons.exit_to_app)),
          ],
        ),
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          // mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            // ElevatedButton(onPressed: _scanwedgePlugin.test, child: const Text('Create profile')),
            Text(_deviceInfo ?? 'No device info', style: Theme.of(context).textTheme.labelSmall),
            Card(
                child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Row(
                // crossAxisAlignment: CrossAxisAlignment.end,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const Text('Disable keystroke'),
                            const SizedBox(width: 5),
                            ValueListenableBuilder(
                                valueListenable: notifierDisableKeystroke,
                                builder: (context, disableKeyboard, _) => Switch(value: disableKeyboard, onChanged: (value) => notifierDisableKeystroke.value = value)),
                          ],
                        ),
                        ValueListenableBuilder(
                            valueListenable: notifierAimType,
                            builder: (context, aimType, _) => PopupMenuButton(
                                  elevation: 4,
                                  padding: const EdgeInsets.all(5),
                                  itemBuilder: ((context) => AimType.values
                                      .map((e) => PopupMenuItem(
                                            value: e,
                                            child: Text(e.toString().split('.').last),
                                          ))
                                      .toList()),
                                  child: Container(
                                    padding: const EdgeInsets.symmetric(vertical: 4, horizontal: 8),
                                    decoration: BoxDecoration(
                                        color: Colors.blue, borderRadius: BorderRadius.circular(5), boxShadow: const [BoxShadow(offset: Offset(2, 2), blurRadius: 1, color: Colors.black54)]),
                                    child: Column(
                                      mainAxisSize: MainAxisSize.min,
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text('AimType:', style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.white)),
                                        Padding(
                                          padding: const EdgeInsets.only(left: 6.0),
                                          child: Text(aimType.toString().split('.').last, style: Theme.of(context).textTheme.titleLarge?.copyWith(color: Colors.white)),
                                        ),
                                      ],
                                    ),
                                  ),
                                  onSelected: (newAimType) => notifierAimType.value = newAimType,
                                )),
                      ],
                    ),
                  ),
                  ElevatedButton(onPressed: _createProfile, child: const Text('Create profile')),
                ],
              ),
            )),
            TextFormField(
              decoration: const InputDecoration(hintText: 'auto inserted if keystroke and focused', contentPadding: EdgeInsets.symmetric(horizontal: 6)),
            ),
            const Expanded(child: SizedBox()),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: _scanwedgePlugin != null
                    ? Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Last scan:'),
                          StreamBuilder(
                              stream: _scanwedgePlugin!.stream,
                              builder: ((context, snapshot) => Text(
                                    snapshot.hasData
                                        ? snapshot.data.toString()
                                        : snapshot.hasError
                                            ? snapshot.error.toString()
                                            : 'Scan something',
                                    style: Theme.of(context).textTheme.titleMedium,
                                  ))),
                        ],
                      )
                    : const Center(child: CircularProgressIndicator()),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
